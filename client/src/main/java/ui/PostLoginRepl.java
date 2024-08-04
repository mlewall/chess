package ui;
import chess.ChessGame;
import client.ChessClient;
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
//    private final ServerFacade server;
//    private boolean signedIn;
//    private String visitorName;

    public PostLoginRepl(ChessClient chessClient) {
        this.chessClient = chessClient;
        chessClient.signedIn = true;
        games = new HashMap<>();
    }

    public void run(){
        System.out.println("Welcome to chess, @" + chessClient.visitorName + "!");
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        String result = "";
        while(chessClient.signedIn && !result.equals("quit")){
            System.out.print("\n" + EscapeSequences.SET_TEXT_BLINKING + ">>> " + EscapeSequences.RESET_TEXT_BLINKING);
            String input = scanner.nextLine();

            try{
                result = eval(input); //sometimes will this print out some kind of gameBoard?
                System.out.print(result);
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

                case "quit" -> "quit"; //you can choose to make them log out or can just quite
                default -> help();
            };
        }
        catch(ResponseException ex){
            return ex.getMessage();
        }
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

    /*Lists all the games that currently exist on the server.
    Calls the server list API to get all the game data, and
    displays the games in a numbered list, including the game
    name and players (not observers) in the game. The numbering for the list should be independent of the game IDs and should start at 1.*/
    public String listGames() throws ResponseException {
        assertSignedIn();
        ArrayList<SimplifiedGameData> gamesOnServer = chessClient.server.listGames();
        //this.currentGames = games; //update the gameList
        this.games.clear(); //start over with a new, updated list
        StringBuilder result = new StringBuilder();
        int i = 1;
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
        String gameNum = params[0]; //this should be a number (1-y)
        String playerColor = params[1].toUpperCase();
        if(!playerColor.equals("WHITE") && !playerColor.equals("BLACK")){
            throw new ResponseException(400, "Invalid color. Please specify [BLACK] or [WHITE]");
        }
        if(isInteger(gameNum) && games.containsKey(Integer.parseInt(gameNum))){
            int gameID = Integer.parseInt(gameNum);
            SimplifiedGameData game = games.get(gameID);
            try {
                JoinGameResult result = chessClient.server.joinGame(playerColor, game.gameID());
                return String.format("You are now joined to game %s as %s.", gameNum, playerColor);
            }
            catch(Exception e){
                throw new ResponseException(400, "Invalid game id or invalid player color");
            }
        }
        else{
            throw new ResponseException(400, "Invalid game number");
        }
    }


    public String observeGame(String...params) throws ResponseException {
        int gameId;
        if (params.length > 0) {
            gameId = Integer.parseInt(params[0]);
            ChessGame newGame = new ChessGame();
            //printGame(newGame);
            //print out a placeholder board -- will connect with websockets later
            return String.format("ChessGame Placeholder");
        }
        throw new ResponseException(400, "Invalid game id");
    }

    public String userLogout() throws ResponseException {
        assertSignedIn();
        chessClient.server.logout();
        chessClient.signedIn = false;
        return String.format("Successfully logged out @%s. \n", chessClient.visitorName);
    }

    private void assertSignedIn() throws ResponseException {
        if (!chessClient.signedIn) {
            throw new ResponseException(400, "You must sign in.");
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
        return """
                1) create <NAME> - create a game
                2) list - games
                3) join <ID> [WHITE | BLACK] - join game
                4) observe <ID> - watch a game
                5) quit - end chess experience
                6) help - get possible commands
                """;
    }



}
