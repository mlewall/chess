package ui;
import chess.ChessGame;
import client.ChessClient;
import client.websocket.WebSocketFacade;
import com.google.gson.Gson;
import model.SimplifiedGameData;
import reqres.*;
import server.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class PostLoginRepl {
    private final ChessClient chessClient;
    private HashMap<Integer, SimplifiedGameData> games;

    public PostLoginRepl(ChessClient chessClient) {
        this.chessClient = chessClient;
        chessClient.signedIn = true;
        games = new HashMap<>();
    }

    public void run(){

        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE +
                "Welcome to chess, @" + chessClient.visitorName + "!"
                + EscapeSequences.RESET_TEXT_COLOR)
        ;
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        String result = "";
        while(chessClient.signedIn && !result.equals("quit")){
            System.out.print("\n" + "[LOGGED_IN] " + ">>> ");
            String input = scanner.nextLine();

            try{
                result = eval(input); //sometimes will this print out some kind of gameBoard?
                if(!result.equals("quit")){
                    System.out.print(result);
                }
            }
            catch(Exception e){
                var msg = e.toString();
                System.out.print(msg);
            }
        }
    }

    public String eval(String input){
        try{
            var tokens = input.split(" ");
            var command = (tokens.length > 0) ? tokens[0] : "help"; //gets rid of the first param (the command)
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch(command){
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "logout" -> userLogout();
                //case "clear" -> clear();

                case "quit" -> "You must log out to quit chess."; //you can choose to make them log out or can just quite
                default -> help();
            };
        }
        catch(ResponseException ex){
            return ex.getMessage();
        }
    }
    public String userLogout() throws ResponseException {
        assertSignedIn();
        chessClient.server.logout();
        chessClient.signedIn = false;
        String out = String.format("Successfully logged out @%s. \n", chessClient.visitorName);
        System.out.println(out);
        return "quit";
    }

    public String createGame(String...params) throws ResponseException {
        assertSignedIn();
        String gameName;
        try{
            if (params.length > 0) {
                gameName = params[0];
                CreateGameResult result = chessClient.server.createGame(gameName);
                return String.format("Game created successfully!");
            }}
        catch(Exception ex){
            return ex.getMessage();
        }
        throw new ResponseException(400, "Invalid game name");
    }

    public String listGames() throws ResponseException {
        assertSignedIn();
        ArrayList<SimplifiedGameData> gamesOnServer = chessClient.server.listGames();
        //this.currentGames = games; //update the gameList
        this.games.clear(); //start over with a new, updated list
        StringBuilder result = new StringBuilder();
        int i = 1;
        result.append(EscapeSequences.SET_TEXT_COLOR_BLUE + "CURRENT GAME LIST:\n" + EscapeSequences.RESET_TEXT_COLOR);
        for(var game : gamesOnServer){
            String white = game.whiteUsername();
            String black = game.blackUsername();
            if(white == null){
                white = "None";
            }
            if(black == null){
                black = "None";
            }
            result.append(i).append(") ");
            result.append("Game Name: ").append(game.gameName()).append(" | ");
            result.append("White Player: ").append(white).append(" | ");
            result.append("Black Player: ").append(black).append("\n");
            this.games.put(i, game);
            i++;
        }
        return result.toString();
    }

    public String joinGame(String...params) throws ResponseException {
        assertSignedIn();
        if (params.length < 2) { //not enough args
            throw new ResponseException(400, "Specify in this format: join <ID> <COLOR>");
        }
        String gameNumStr = params[0]; //this should be a number (1-y)
        String playerColor = params[1].toUpperCase();
        if(!playerColor.equals("WHITE") && !playerColor.equals("BLACK")){
            throw new ResponseException(400, "Invalid color. Please specify [BLACK] or [WHITE]");
        }
        if(isInteger(gameNumStr) && games.containsKey(Integer.parseInt(gameNumStr))){
            int gameNum = Integer.parseInt(gameNumStr);
            SimplifiedGameData game = games.get(gameNum);
            try {
                JoinGameResult result = chessClient.server.joinGame(playerColor, game.gameID());
                String confirmation = String.format("You are now joined to game %s as %s.", gameNumStr, playerColor);

                //make gameplay, establish a websocket connection with the server, send connect msg, run the loop
                GameplayRepl gamePlay = new GameplayRepl(playerColor, chessClient, game.gameID());
                //don't step through these 4-ish lines
                chessClient.setNotificationHandler(gamePlay);
                WebSocketFacade wsf = new WebSocketFacade(chessClient.serverUrl, gamePlay);
                chessClient.setWebSocketFacade(wsf);
                gamePlay.setWebSocketFacade(wsf);
                wsf.connect(chessClient.server.authToken, game.gameID());
                gamePlay.run();

                return confirmation;
            }
            catch(ResponseException e){
                if(e.getStatusCode() == 403){
                    throw new ResponseException(403, "Color already taken. Please choose another game/color to join.");
                }
                return "Please specify a different game/color to join";
            }
        }
        else{
            throw new ResponseException(400, "Invalid game number.");
        }
    }


    public String observeGame(String...params) throws ResponseException {
        if (params.length > 0) {
            String gameNum = params[0];
            if(isInteger(gameNum) && games.containsKey(Integer.parseInt(gameNum))){
                int gameID = Integer.parseInt(gameNum);
                SimplifiedGameData game = games.get(gameID); //this game WILL be used later. Probably w websockets
                //todo: establish a websocket connection

                GameplayRepl gamePlay = new GameplayRepl("WHITE", chessClient, game.gameID());

                //don't step through these 4-ish lines
                chessClient.setNotificationHandler(gamePlay);
                WebSocketFacade wsf = new WebSocketFacade(chessClient.serverUrl, gamePlay);
                chessClient.setWebSocketFacade(wsf);
                gamePlay.setWebSocketFacade(wsf);
                wsf.connect(chessClient.server.authToken, game.gameID());
                gamePlay.run();

                return "Observing game #" + gameNum;
            }
        return "Invalid game number, please enter a game number from the gamelist.";
    }
        return "Missing or invalid game number. ";
    }

    private void assertSignedIn() throws ResponseException {
        if (!chessClient.signedIn) {
            //userLogout();
            throw new ResponseException(400, "An authorization error occurred. Please sign in again.");
        }
    }

    private boolean isInteger(String str){
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String help(){
        return EscapeSequences.SET_TEXT_COLOR_BLUE +
                EscapeSequences.SET_TEXT_UNDERLINE +
                "CHESS MAIN MENU \n" +
                EscapeSequences.RESET_TEXT_COLOR +
                EscapeSequences.RESET_TEXT_UNDERLINE +
                """
                1) create <name> - create a game
                2) list - games
                3) join <game number> [WHITE | BLACK] - join game
                4) observe <game number> - watch a game
                5) logout - log out of current session
                6) help - get possible commands
                """;
    }

}
