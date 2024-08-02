package client;

import client.websocket.NotificationHandler;
import reqres.LoginResult;
import server.ResponseException;
import server.ServerFacade;

import java.util.Arrays;

public class ChessClient {
    private String visitorName = null;
    private final ServerFacade server; //note that a new one of these is made for every new Client
    private final String serverUrl;
    private final NotificationHandler notificationHandler;
    private String authToken; //this is initialized when the user logs in successfully
    //should be cleared upon logout

    //private WebSocketFacade ws;
    private State state = State.SIGNEDOUT;

    public ChessClient(String serverUrl, NotificationHandler notificationHandler) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler; // this is actually a pointer to the repl
    }

    public String eval(String input){
        try{
            var tokens = input.toLowerCase().split(" ");
            var command = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            System.out.println(command + " " + params);
//            return switch(command){
//                case "login": logIn(params);
//                case ""
//            }
        }
    }

    public void userLogin(String username, String password) throws ResponseException {
        LoginResult result = server.login(username, password);
        this.visitorName = username;
    }




}
