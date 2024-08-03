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
    private String visitorName = null;
    private final ServerFacade server; //note that a new one of these is made for every new Client
    private final String serverUrl;
    private final NotificationHandler notificationHandler;
    //should be cleared upon logout

    //private WebSocketFacade ws;
    private State state = State.SIGNEDOUT;
    private ChessGame localChessCopy;
    private HashMap<Integer, SimplifiedGameData> currentGames;

    public ChessClient(String serverUrl, NotificationHandler notificationHandler) {
        this.serverUrl = serverUrl;
        server = new ServerFacade(serverUrl);
        this.notificationHandler = notificationHandler; // this is actually a pointer to the repl
        this.currentGames = new HashMap<>();
    }

    public String eval(String input){
        try{
            var tokens = input.toLowerCase().split(" ");
            var command = (tokens.length > 0) ? tokens[0] : "help";

            //gets rid of the first param (the command)
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return command + " " + Arrays.toString(params);
            return switch(command){
                case "register" -> registerUser(params);
                case "login" -> userLogin(params);

                case "create" -> createGame(params);
                case "list" -> listGames();
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "logout" -> userLogout();

                case "quit" -> "quit";
                default -> help();
            };
        }
        catch(ResponseException ex){
            return ex.getMessage();
        }
    }

    public String registerUser(String...params) throws ResponseException {
        String username = null;
        String password = null;
        String email = null;
        if(params.length > 2){
            username = params[0];
            password = params[1];
            email = params[2];
            server.register(username, password, email);
            //they are automatically logged in when they register (an authToken is generated)?
            //todo: what happens if the credentials are wrong?
            this.visitorName = username;
            return String.format("Successfully registered %s", username);
        }
        throw new ResponseException(400, "Expected <username> <password> <email>");
    }

    public String userLogin(String...params) throws ResponseException {
        String username = null;
        String password = null;
        if (params.length > 1) {
            username = params[0];
            password = params[1];
            LoginResult result = server.login(username, password);
            this.visitorName = username;
            return String.format("You are now signed in as %s.", visitorName);
        }
        throw new ResponseException(400, "Invalid username or password");
    }

    public String createGame(String...params) throws ResponseException {
        assertSignedIn();
        String gameName = null;
        if (params.length > 0) {
            gameName = params[0];
            CreateGameResult result = server.createGame(gameName);
        }
        throw new ResponseException(400, "Invalid game name");
    }

    public String listGames() throws ResponseException {
        assertSignedIn();
        ArrayList<SimplifiedGameData> games = server.listGames();
        this.currentGames = games; //update the gameList
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

        }
    }


    public String help(){
        if (state == State.SIGNEDOUT){
            return  """
               1) register <USERNAME> <PASSWORD> <EMAIL> - to create an account
               2) login <USERNAME> <PASSWORD> - to play chess
               3) quit - playing chess
               4) help - with possible commands
               """;
            }
        return """
                1) create <NAME> - create a game
                2) list - games
                3) join <ID> [WHITE | BLACK] - join game
                4) observe <ID> - watch a game
                5) quit - end chess experience
                6) help - get possible commands
                """;
        }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(400, "You must sign in");
        }
    }

    public void updateGameList(ArrayList<SimplifiedGameData> games){
        currentGames.clear();
        for
    }
    }





