package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import websocket.commands.UserGameCommand;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketHandler {
    //WebSocketSessions sessions; (this is where I used to keep the retrieval methods)


    public final ConcurrentHashMap<Integer, HashSet<Session>> connections;

    public WebSocketHandler() {
        //this.sessions = sessions;
        this.connections = new ConcurrentHashMap<>();
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {}

    @OnWebSocketClose
    public void onClose(Session session){}

    @OnWebSocketError
    public void onError(Throwable error) {}

    @OnWebSocketMessage
    public void onMessage(Session session, String message) { //SWITCH determine message type
        WebSocketService service = new WebSocketService();
        //is the String message a json representation of the UserGameCommand?
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class); //deserialize the UserGameCommand
        switch(command.getCommandType()){
            case CONNECT -> service.connect(command);
            case MAKE_MOVE -> service.makeMove(command);
            case LEAVE -> service.leaveGame(command);
            case RESIGN -> service.resignGame(command);
        }
    }

    void sendMessage(String message, Session session) throws IOException {
        //todo: use this in broadcast,
        session.getRemote().sendString(message);
    }

    void broadcastMessage(int gameID, String message, Session exceptThisSession){

    }
}
