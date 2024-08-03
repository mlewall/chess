package client;

import com.google.gson.Gson;
import model.SimplifiedGameData;
import reqres.CreateGameResult;
import reqres.JoinGameResult;
import server.ResponseException;
import server.ServerFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class PostLoginRepl {
    private final ChessClient chessClient;
    private final ServerFacade server;
    private boolean signedIn;
    private String visitorName;

    public PostLoginRepl(ChessClient chessClient, ServerFacade server, String visitorName) {
        this.chessClient = chessClient;
        this.server = server;
        this.visitorName = visitorName;
        signedIn = true;
    }

    public void run(){
        System.out.println("Welcome to chess, @" + visitorName + "!");
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        String result = "";
        while(signedIn && !result.equals("quit")){
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
                case "clear" -> clear();

                case "quit" -> "quit"; //you can choose to make them log out or can just quite
                default -> help();
            };
        }
        catch(ResponseException ex){
            return ex.getMessage();
        }
    }

    public String clear() throws ResponseException{
        server.clear();
        return "Server has been reset";
    }

    public String createGame(String...params) throws ResponseException {
        assertSignedIn();
        String gameName = null;
        try{
            if (params.length > 0) {
                gameName = params[0];
                CreateGameResult result = server.createGame(gameName);
                return String.format("Game created! Game ID: " + result.gameID());
            }}
        catch(Exception ex){
            return ex.getMessage();
        }
        throw new ResponseException(400, "Invalid game name");
    }

    public String listGames() throws ResponseException {
        assertSignedIn();
        ArrayList<SimplifiedGameData> games = server.listGames();
        //this.currentGames = games; //update the gameList
        StringBuilder result = new StringBuilder();
        Gson gson = new Gson();
        for(var game : games){
            result.append(gson.toJson(game)).append("\n");
        }
        return result.toString();
    }

    public String joinGame(String...params) throws ResponseException {
        assertSignedIn();
        String playerColor;
        int gameId;
        //todo: figure out what this means:
        // Your client will need to keep track of which number corresponds to which game from the last time it listed the games.
        if (params.length > 1) {
            playerColor = params[0].toUpperCase();
            gameId = Integer.parseInt(params[1]);
            JoinGameResult result = server.joinGame(playerColor, gameId);
            return String.format("You are now joined to game %s as %s.", gameId, playerColor);
        }
        throw new ResponseException(400, "Invalid game id or invalid player color");
    }

    public String observeGame(String...params) throws ResponseException {
        int gameId;
        if (params.length > 0) {
            gameId = Integer.parseInt(params[0]);
            //SimplifiedGameData game = lookupGame(gameId); //the problem is that this doesn't
            // contain the serialized chessGame
            //todo: figure out how to access game Data for real...
            // key Q: can the HTTP response include the gameData always? Can that be kept in the hashmap?
            //ChessGame reqGame = game.game
            //newChessGame -> print
            //print out a placeholder board -- will connect with websockets later
            return String.format("ChessGame Placeholder");
        }
        throw new ResponseException(400, "Invalid game id");
    }

    public String userLogout() throws ResponseException {
        assertSignedIn();
        server.logout();
        signedIn = false;
        return String.format("Successfully logged out %s", visitorName);
    }

    private void assertSignedIn() throws ResponseException {
        if (!signedIn) {
            throw new ResponseException(400, "You must sign in");
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
