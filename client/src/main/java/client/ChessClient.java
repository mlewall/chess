package client;

import com.sun.nio.sctp.NotificationHandler;
import reqres.LoginResult;
import server.ResponseException;
import server.ServerFacade;

public class ChessClient {
    private String visitorName = null;
    private final ServerFacade server;
    private final String serverUrl;
    private final NotificationHandler notificationHandler;
    private String authToken; //this is initialized when the user logs in successfully
    //should be cleared upon logout

    //private WebSocketFacade ws;
    private State state = State.SIGNEDOUT;

    public ChessClient(String serverUrl, NotificationHandler notificationHandler) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
    }

    public void userLogin(String username, String password) throws ResponseException {
        LoginResult result = server.login(username, password);
    }




}
