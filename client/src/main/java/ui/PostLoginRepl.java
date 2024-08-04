package ui;
import client.ChessClient;
import com.google.gson.Gson;
import model.SimplifiedGameData;
import reqres.*;
import server.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class PostLoginRepl {
    private final ChessClient chessClient;
//    private final ServerFacade server;
//    private boolean signedIn;
//    private String visitorName;

    public PostLoginRepl(ChessClient chessClient) {
        this.chessClient = chessClient;
        chessClient.signedIn = true;
    }

    public void run(){
        System.out.println("Welcome to chess, @" + chessClient.visitorName + "!");
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        String result = "";
        while(chessClient.signedIn && !result.equals("quit")){
            System.out.print("\n" + ">>> ");
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
                return String.format("Game created! Game ID: " + result.gameID());
            }}
        catch(Exception ex){
            return ex.getMessage();
        }
        throw new ResponseException(400, "Invalid game name");
    }

    public String listGames() throws ResponseException {
        assertSignedIn();
        ArrayList<SimplifiedGameData> games = chessClient.server.listGames();
        //this.currentGames = games; //update the gameList
        StringBuilder result = new StringBuilder();
        Gson gson = new Gson();
        for(var game : games){
            result.append(gson.toJson(game)).append("\n");
        }
        return result.toString();
    }

    public String joinGame(String...params) throws ResponseException {
//        assertSignedIn();
        if (params.length < 2) {
            throw new ResponseException(400, "Expected <COLOR> <GAME_ID>");
        }
        String playerColor = params[1].toUpperCase();
        String gameId = params[0];
        if(!playerColor.equals("WHITE") && !playerColor.equals("BLACK")){
            throw new ResponseException(400, "Invalid color");
        }
        if(!isInteger(gameId)){
            throw new ResponseException(400, "Invalid game ID: please include a number");
        }
        int gameID = Integer.parseInt(gameId);

        try {
            JoinGameResult result = chessClient.server.joinGame(playerColor, gameID);
            return String.format("You are now joined to game %s as %s.", gameId, playerColor);
        }
        catch(Exception e){
            throw new ResponseException(400, "Invalid game id or invalid player color");
        }
    }
        //todo: figure out what this means:
        // Your client will need to keep track of which number corresponds to which game from the last time it listed the games.


    public String observeGame(String...params) throws ResponseException {
        int gameId;
        if (params.length > 0) {
            gameId = Integer.parseInt(params[0]);
            //newChessGame -> print
            //print out a placeholder board -- will connect with websockets later
            return String.format("ChessGame Placeholder");
        }
        throw new ResponseException(400, "Invalid game id");
    }

    public String userLogout() throws ResponseException {
        assertSignedIn();
        chessClient.server.logout();
        chessClient.signedIn = false;
        return String.format("Successfully logged out @%s \n", chessClient.visitorName);
    }

    private void assertSignedIn() throws ResponseException {
        if (!chessClient.signedIn) {
            throw new ResponseException(400, "You must sign in");
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
