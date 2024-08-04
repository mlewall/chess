package client;

import chess.ChessGame;
import client.websocket.NotificationHandler;
import com.google.gson.Gson;
import model.SimplifiedGameData;
import reqres.*;
import server.ResponseException;
import server.ServerFacade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ChessClient {
    public String visitorName;
    public final ServerFacade server; //note that a new one of these is made for every new Client
    private final String serverUrl;
    public boolean signedIn = false;
    public ChessGame localChessCopy;
    private HashMap<Integer, SimplifiedGameData> currentGames;

    //private final NotificationHandler notificationHandler;
    //private WebSocketFacade ws;

    public ChessClient(String serverUrl, NotificationHandler notificationHandler) {
        this.serverUrl = serverUrl;
        server = new ServerFacade(serverUrl);
        //this.notificationHandler = notificationHandler; // this is actually a pointer to the repl
        //this.currentGames = new HashMap<>();
    }


//    public String eval(String input){
//        try{
//            var tokens = input.split(" ");
//            var command = (tokens.length > 0) ? tokens[0] : "help";
//
//            //gets rid of the first param (the command)
//            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
//            return switch(command){
//                case "register" -> registerUser(params);
//                case "login" -> userLogin(params);
//
//                case "create" -> createGame(params);
//                case "list" -> listGames();
//                case "join" -> joinGame(params);
//                case "observe" -> observeGame(params);
//                case "logout" -> userLogout();
//                case "clear" -> clear();
//
//                case "quit" -> "quit"; //you can choose to make them log out or can just quite
//                default -> help();
//            };
//        }
//        catch(ResponseException ex){
//            return ex.getMessage();
//        }
//    }
//    public String clear() throws ResponseException{
//        server.clear();
//        return "Server has been reset";
//    }
//
//    public String registerUser(String...params) throws ResponseException {
//        String username = null;
//        String password;
//        String email;
//        if(params.length > 2){
//            username = params[0];
//            password = params[1];
//            email = params[2];
//            //try
//            server.register(username, password, email);
//            //they are automatically logged in when they register (an authToken is generated)?
//            //todo: what happens if the credentials are wrong?
//            this.visitorName = username;
//            signedIn = true;
//            return String.format("You were successfully registered and logged in as: %s \n", username);
//            //catch
//            //--specify specific errors/input problems
//        }
//
//        throw new ResponseException(400, "Expected <username> <password> <email>");
//    }
//
//    public String userLogin(String...params) throws ResponseException {
//        String username = null;
//        String password = null;
//        if (params.length > 1) {
//            username = params[0];
//            password = params[1];
//            LoginResult result = server.login(username, password);
//            this.visitorName = username;
//            signedIn = true;
//            return String.format("You are now signed in as %s.", visitorName);
//        }
//        throw new ResponseException(400, "Invalid username or password");
//    }
//
//    public String createGame(String...params) throws ResponseException {
//        assertSignedIn();
//        String gameName = null;
//        try{
//        if (params.length > 0) {
//            gameName = params[0];
//            CreateGameResult result = server.createGame(gameName);
//            return String.format("Game created! Game ID: " + result.gameID());
//        }}
//        catch(Exception ex){
//            return ex.getMessage();
//        }
//        throw new ResponseException(400, "Invalid game name");
//    }
//
//    public String listGames() throws ResponseException {
//        assertSignedIn();
//        ArrayList<SimplifiedGameData> games = server.listGames();
//        //this.currentGames = games; //update the gameList
//        StringBuilder result = new StringBuilder();
//        Gson gson = new Gson();
//        for(var game : games){
//            result.append(gson.toJson(game)).append("\n");
//        }
//        return result.toString();
//    }
//
//    public String joinGame(String...params) throws ResponseException {
//        assertSignedIn();
//        String playerColor;
//        int gameId;
//        //todo: figure out what this means:
//        // Your client will need to keep track of which number corresponds to which game from the last time it listed the games.
//        if (params.length > 1) {
//            playerColor = params[0].toUpperCase();
//            gameId = Integer.parseInt(params[1]);
//            JoinGameResult result = server.joinGame(playerColor, gameId);
//            return String.format("You are now joined to game %s as %s.", gameId, playerColor);
//        }
//        throw new ResponseException(400, "Invalid game id or invalid player color");
//    }
//
//    public String observeGame(String...params) throws ResponseException {
//        int gameId;
//        if (params.length > 0) {
//            gameId = Integer.parseInt(params[0]);
//            //SimplifiedGameData game = lookupGame(gameId); //the problem is that this doesn't
//            // contain the serialized chessGame
//            //todo: figure out how to access game Data for real...
//            // key Q: can the HTTP response include the gameData always? Can that be kept in the hashmap?
//            //ChessGame reqGame = game.game
//            //newChessGame -> print
//            //print out a placeholder board -- will connect with websockets later
//            return String.format("ChessGame Placeholder");
//        }
//        throw new ResponseException(400, "Invalid game id");
//    }
//
//    public String userLogout() throws ResponseException {
//        assertSignedIn();
//        server.logout();
//        signedIn = false;
//        return String.format("Successfully logged out %s", visitorName);
//    }
//
//
//    public String help(){
//        if (!signedIn){
//            return  """
//               1) register <USERNAME> <PASSWORD> <EMAIL> - to create an account
//               2) login <USERNAME> <PASSWORD> - to play chess
//               3) quit - playing chess
//               4) help - with possible commands
//               """;
//            }
//        return """
//                1) create <NAME> - create a game
//                2) list - games
//                3) join <ID> [WHITE | BLACK] - join game
//                4) observe <ID> - watch a game
//                5) quit - end chess experience
//                6) help - get possible commands
//                """;
//        }
//
//    private void assertSignedIn() throws ResponseException {
//        if (!signedIn) {
//            throw new ResponseException(400, "You must sign in");
//        }
//    }


//    public void updateGameList(ArrayList<SimplifiedGameData> games){
//        currentGames.clear();
//        for(SimplifiedGameData game : games){
//            currentGames.put(game.gameID(), game);
//        }
//    }
//
//    public SimplifiedGameData lookupGame(int gameID){
//        return currentGames.get(gameID);
//    }
    }





