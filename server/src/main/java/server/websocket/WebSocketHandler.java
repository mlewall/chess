package server.websocket;

import chess.ChessMove;
import chess.ChessPosition;
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

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
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
        //todo: do I need to make a specific type adapter?

        try {
            switch (command.getCommandType()) {
                case CONNECT -> connectHandler(service, command, session, connections);
                case MAKE_MOVE -> { MoveCommand moveCmd = new Gson().fromJson(message, MoveCommand.class);
                        makeMoveHandler(service, moveCmd, session, connections);}
                case LEAVE -> leaveGameHandler(service, command, session, connections);
                case RESIGN -> resignHandler(service, command, session, connections);
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
    void connectHandler(WebSocketService service, UserGameCommand command, Session session,
                     ConcurrentHashMap<Integer, HashSet<Session>> connections) throws DataAccessException, IOException {
        LoadGameMessage result = (LoadGameMessage) service.connect(command, session, connections);
        String jsonResult = new Gson().toJson(result); //should just contain the chessGame and that it's a loadgame type

        String teamColor = service.getTeamColor(command);
        String username = service.getUsername(command);

        NotificationMessage notificationMessage;
        if(teamColor != null){
            notificationMessage = new NotificationMessage(NOTIFICATION,
                    username + " has connected to the game as " + teamColor);
        } else{
            notificationMessage = new NotificationMessage(NOTIFICATION,
                    username + " has connected to the game as an observer.");
        }
        String jsonNotification = new Gson().toJson(notificationMessage);

        sendMessage(jsonResult, session); //this sends the LOAD_GAME message back to the root client
        broadcastMessage(command.getGameID(), jsonNotification, session);
        }


    void makeMoveHandler(WebSocketService service, UserGameCommand command, Session session, ConcurrentHashMap<Integer,
            HashSet<Session>> connections) throws DataAccessException, InvalidMoveException, IOException {
        LoadGameMessage result = (LoadGameMessage) service.makeMove((MoveCommand) command, session, connections);
        GameEndStatus status = service.determineStaleCheckMate(result.getGame()); //make a class that determines if something is in check/checkmate/stalemate

        String jsonLoadGame = new Gson().toJson(result);
        String username = service.getUsername(command);

        String formattedMove = formatMove(((MoveCommand) command).getChessMove());
        NotificationMessage notificationMessage = new NotificationMessage(NOTIFICATION,
                username + " moved " + formattedMove); //todo: fix this formatting haha
        String jsonDidMoveNotification = new Gson().toJson(notificationMessage);

        //1) LOAD_GAME message to all clients in the game (INCLUDING the root client) with an updated game.
        broadcastMessage(command.getGameID(), jsonLoadGame, null);

        //2) Server sends a Notification message to all OTHER clients in that game informing them what move was made.
        broadcastMessage(command.getGameID(), jsonDidMoveNotification, session); //broadcast that message to everyone BUT this session

        //todo: determine order (I think CheckMate is a subset of Check so it goes first)
        if(status.inCheckMate){
            NotificationMessage gameStatus = new NotificationMessage(NOTIFICATION, "Game is in CheckMate!");
            String jsonGameStatus = new Gson().toJson(gameStatus);
            broadcastMessage(command.getGameID(), jsonGameStatus, null);
        }
        else if(status.inCheck){
            NotificationMessage gameStatus = new NotificationMessage(NOTIFICATION, "Game is in Check!");
            String jsonGameStatus = new Gson().toJson(gameStatus);
            broadcastMessage(command.getGameID(), jsonGameStatus, null);
        }
        else if(status.inStaleMate){
            NotificationMessage gameStatus = new NotificationMessage(NOTIFICATION, "Game is in StaleMate!");
            String jsonGameStatus = new Gson().toJson(gameStatus);
            broadcastMessage(command.getGameID(), jsonGameStatus, null);
            }
        }


    void leaveGameHandler(WebSocketService service, UserGameCommand command, Session session,
                       ConcurrentHashMap<Integer, HashSet<Session>> connections) throws DataAccessException, IOException {
        String username = service.getUsername(command);
        service.leaveGame(command, session, connections); //game is updated to remove the root client (void)
        NotificationMessage notificationMessage = new NotificationMessage(NOTIFICATION, username + " has left the game.");
        String jsonNotification = new Gson().toJson(notificationMessage);

        //sends a notification telling all OTHER players that the root client (username) left.
        broadcastMessage(command.getGameID(), jsonNotification, session);
        //todo: determine if it's better to catch and rethrow error to deal with in the switch statement
        //todo: or if it's better to deal with it here...and make a more standard error handling.
        //todo: a new error class for the handlers specifically? Catches stuff from service and packages for websocket?
    }

    void resignHandler(WebSocketService service, UserGameCommand command, Session session,
                    ConcurrentHashMap<Integer, HashSet<Session>> connections) throws DataAccessException, IOException {
        //server marks the game as over (no more moves can be made), //updates game in the DB
        service.resignGame(command, session, connections);
        String username = service.getUsername(command);
        NotificationMessage notification = new NotificationMessage(NOTIFICATION, username + " has resigned.");
        String jsonNotification = new Gson().toJson(notification);
        //notification message to ALL clients in the game informing them that root client resigned
        broadcastMessage(command.getGameID(), jsonNotification, null);
    }

    void sendMessage(String message, Session session) throws IOException {
        //todo: use this in broadcast,
        session.getRemote().sendString(message);
    }

    void broadcastMessage(int gameID, String message, Session exceptThisSession) throws IOException {
            //for every session connected to a gameID EXCEPT this session
        if(exceptThisSession != null){
            for(Session session : connections.get(gameID)){
                if(!session.equals(exceptThisSession)){
                    sendMessage(message, session);
                }
            }
        }
        else{
            for(Session session : connections.get(gameID)){
                 sendMessage(message, session);
            }
        }
    }

    private String formatMove(ChessMove move){
        //for the columns: ("a") a2 a4 (col,row)
        StringBuilder sb = new StringBuilder();
        HashMap<Integer, String> numToLetter = new HashMap<>();
        String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h"};
        int i = 1; //board number //chesspositions are 1-indexed
        for(String letter: letters){
            numToLetter.put(i, letter); //a: 1, etc.
            i++;
        }
        ChessPosition origin = move.getStartPosition();
        ChessPosition destination = move.getEndPosition();
        sb.append(numToLetter.get(origin.getColumn())).append((origin.getRow())); //
        sb.append(" ");
        sb.append(numToLetter.get(destination.getColumn())).append((destination.getRow()));
        return sb.toString();
        }


}


