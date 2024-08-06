package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;

public class WebSocketHandler {
    WebSocketSessions sessions;

    WebSocketHandler(WebSocketSessions sessions) {
        this.sessions = sessions;
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {}

    @OnWebSocketClose
    public void onClose(Session session){
        //todo: might want to add a status code in here; might be useful
    }

    @OnWebSocketError
    public void onError(Throwable error) {
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        //determine message type
        // call one of these methods to process the message
    }
}
