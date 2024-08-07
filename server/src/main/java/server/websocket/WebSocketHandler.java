package server.websocket;

import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.MoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.management.Notification;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static websocket.messages.ServerMessage.ServerMessageType.*;

@WebSocket
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
    public void onClose(Session session, int statusCode, String reason){}

    @OnWebSocketError
    public void onError(Throwable error) {}

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws DataAccessException, IOException { //SWITCH determine message type
        WebSocketService service = new WebSocketService();

        //is the String message a json representation of the UserGameCommand? (yes, probably)
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class); //deserialize the UserGameCommand
        //ServerMessage result;

        try {
            switch (command.getCommandType()) {
                case CONNECT -> execConnect(service, command, session, connections);
                case MAKE_MOVE -> execMakeMove(service, command, session, connections);
                case LEAVE -> execLeaveGame(service, command, session, connections);
                case RESIGN -> execResign(service, command, session, connections);
            };
        }
        catch (Exception e) {
            //will this catch any errors thrown by the below functions and send their error message along?
            ServerMessage errorMsg = new ErrorMessage(ERROR, "error: " + e.getMessage());
            String jsonError = new Gson().toJson(errorMsg);
            sendMessage(jsonError, session);
        }
    }

    //made these separate functions because they have different message handling requirements.
    //However, I think some of this could be incorporated into the switch statement.
    void execConnect(WebSocketService service, UserGameCommand command, Session session,
                     ConcurrentHashMap<Integer, HashSet<Session>> connections) throws DataAccessException {
        LoadGameMessage result = (LoadGameMessage) service.connect(command, session, connections);

        String jsonResult = new Gson().toJson(result); //should just contain the chessGame and that it's a loadgame type

        String username = service.getUsername(command);
        NotificationMessage notificationMessage = new NotificationMessage(NOTIFICATION,
                username + "has connected to the game");
        String jsonNotification = new Gson().toJson(notificationMessage);
        try{
            sendMessage(jsonResult, session); //this sends the load game message back to the root client
            broadcastMessage(command.getGameID(), jsonNotification, session);
        }
        catch (IOException e){
            //todo: figure out what to do with this specifically.
            ServerMessage errorMsg = new ErrorMessage(ERROR, "error:" + e.getMessage());
            String jsonError = new Gson().toJson(errorMsg);
            try {
                sendMessage(jsonError, session);
            }
            catch(IOException e1){
                e.printStackTrace();
            }
        }
    }

    void execMakeMove(WebSocketService service, UserGameCommand command, Session session,
                      ConcurrentHashMap<Integer, HashSet<Session>> connections) throws DataAccessException, InvalidMoveException {
        service.makeMove((MoveCommand) command, session, connections);
    }

    void execLeaveGame(WebSocketService service, UserGameCommand command, Session session,
                       ConcurrentHashMap<Integer, HashSet<Session>> connections){
        service.leaveGame(command, session, connections);
    }

    void execResign(WebSocketService service, UserGameCommand command, Session session,
                    ConcurrentHashMap<Integer, HashSet<Session>> connections){

    }

    void sendMessage(String message, Session session) throws IOException {
        //todo: use this in broadcast,
        session.getRemote().sendString(message);
    }

    void broadcastMessage(int gameID, String message, Session exceptThisSession) throws IOException {
            //for every session connected to a gameID EXCEPT this session
        for(Session session : connections.get(gameID)){
            if(!session.equals(exceptThisSession)){
                sendMessage(message, session);
            }
        }
    }
}
