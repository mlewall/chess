package client;

import chess.ChessGame;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import server.ServerFacade;

public class ChessClient {
    public String visitorName;
    public final ServerFacade server; //note that a new one of these is made for every new Client
    private final String serverUrl;
    public boolean signedIn;
    public ChessGame localChessCopy;

    private final NotificationHandler notificationHandler;
    private WebSocketFacade ws;

    public ChessClient(String serverUrl, NotificationHandler notificationHandler) {
        this.serverUrl = serverUrl;
        this.server = new ServerFacade(serverUrl);
        this.signedIn = false;
        this.notificationHandler = notificationHandler; // this is actually a pointer to the repl
        //this.currentGames = new HashMap<>();
    }

}





