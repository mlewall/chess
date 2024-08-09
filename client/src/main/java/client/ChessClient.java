package client;

import chess.ChessGame;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import server.ResponseException;
import server.ServerFacade;

public class ChessClient {
    public String visitorName;
    public final ServerFacade server; //note that a new one of these is made for every new Client
    private final String serverUrl;
    public boolean signedIn;
    public ChessGame localChessCopy;

    public NotificationHandler notificationHandler;
    public WebSocketFacade ws;

    public ChessClient(String serverUrl) {
        this.serverUrl = serverUrl;
        this.server = new ServerFacade(serverUrl);
        this.signedIn = false;
        //this.notificationHandler = new NotificationHandler(); // this is actually a pointer to the repl
        //this.currentGames = new HashMap<>();
    }

    public void setNotificationHandler(NotificationHandler notificationHandler) throws ResponseException {
        this.notificationHandler = notificationHandler;
        this.ws = new WebSocketFacade(serverUrl, notificationHandler);

    }




}





