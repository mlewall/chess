package server.websocket;

    /* This contains the functions that are called from the WebSocket handler.
    *
    * 1) connect(message): call service and/or send messages to client
    * 2) makeMove(message):
    * 3) leaveGame(message)
    * 4) resignGame(message)
    *
    * These methods are responsible for actually connecting to the DAOs and getting data and stuff.
    * Similar to the functionality of the services we wrote from phase 3.
    *  */

import dataaccess.database.SqlAuthDao;
import dataaccess.database.SqlGameDao;
import dataaccess.database.SqlUserDao;
import model.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketException;
import websocket.commands.UserGameCommand;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import dataaccess.*;

import javax.xml.crypto.Data;

public class WebSocketService {
    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;

    WebSocketService() throws DataAccessException{
        try {
            userDAO = new SqlUserDao();
            gameDAO = new SqlGameDao();
            authDAO = new SqlAuthDao();
        }
        catch (DataAccessException e){
            throw new DataAccessException(500, "Error instantiating WebSocketService. Error in: " + e.getMessage());
        }
    }
    //note that a command contains: commandType, authToken, gameID, and maybe a Move (if we're in makeMove)

    //should these all send back some kind of server message?

    public String connect(UserGameCommand command, Session session, ConcurrentHashMap<Integer, HashSet<Session>> connections) throws DataAccessException {
        //client sends a connect message (contains commandType, gameID, authToken)
        //we don't need to worry about adding players here because they've already been added via http

        //get Username from dao (based on authToken)
        String username = authDAO.getAuthData(command.getAuthToken()).username();
        if(username == null){
            throw new DataAccessException(401, "Username not found in database; invalid authToken");
        }
        GameData game = gameDAO.getGame(command.getGameID());
        HashSet<Session> sessions = connections.computeIfAbsent(command.getGameID(), k -> new HashSet<>());
        //

        boolean added = sessions.add(session); //adds this user's session to the collection of sessions associated with the game ID
        if(!added){
            throw new DataAccessException(403, "Session already associated with game");
        }

        //server sends a load game message back to root client
        //server sends a notification message to all other clients
        // in that game informing them the root client connected to the game,
        // either as a player (in which case their color must be specified) or as an observer.
        return null;
    }

    public String makeMove(UserGameCommand command, Session session, ConcurrentHashMap<Integer, HashSet<Session>> connections){
        //verifies validity of the move
        return null;
    }

    public String leaveGame(UserGameCommand command, Session session, ConcurrentHashMap<Integer, HashSet<Session>> connections){
        return null;
    }

    public String resignGame(UserGameCommand command, Session session, ConcurrentHashMap<Integer, HashSet<Session>> connections){
        return null;
    }


}
