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

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import dataaccess.database.SqlAuthDao;
import dataaccess.database.SqlGameDao;
import dataaccess.database.SqlUserDao;
import model.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketException;
import websocket.commands.MoveCommand;
import websocket.commands.UserGameCommand;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import dataaccess.*;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;

import javax.xml.crypto.Data;

import static websocket.messages.ServerMessage.ServerMessageType.*;

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
    //client sends server a connect message (contains commandType, gameID, authToken)
    //we don't need to worry about adding players here because they've already been added via http
    public ServerMessage connect(UserGameCommand command, Session session, ConcurrentHashMap<Integer, HashSet<Session>> connections) throws DataAccessException {
        //get Username from dao (based on authToken)
        AuthData authData = authDAO.getAuthData(command.getAuthToken());
        if(authData == null){
            throw new DataAccessException(401, "Username not found in database; invalid authToken");
        }
        String username = authData.username();

        //we are assuming the gameID is valid (it should have been validated when they specify the game)
        HashSet<Session> sessions = connections.computeIfAbsent(command.getGameID(), k -> new HashSet<>());

        boolean added = sessions.add(session); //adds this user's session to the collection of sessions associated with the game ID
        if(!added){
            throw new DataAccessException(403, "Session already associated with game");
        }
        GameData game = gameDAO.getGame(command.getGameID());
        ChessGame chessGame = game.game();
        LoadGameMessage gameMessage = new LoadGameMessage(LOAD_GAME, chessGame);
        return gameMessage;

    }

    public String makeMove(MoveCommand command, Session session,
                           ConcurrentHashMap<Integer, HashSet<Session>> connections) throws DataAccessException, InvalidMoveException {
        //validate user (this could be factored out)
        String username = authDAO.getAuthData(command.getAuthToken()).username();
        if(username == null){
            throw new DataAccessException(401, "Username not found in database; invalid authToken");
        }
        //get game
        GameData gameData = gameDAO.getGame(command.getGameID());
        if(gameData == null){
            throw new DataAccessException(403, "Game not found in database; invalid gameID");
        }
        ChessGame game = gameData.game();
        ChessMove move = command.getChessMove();
        Collection<ChessMove> possMoves = game.validMoves(move.getStartPosition());
        if(!possMoves.contains(move)){ //user requested an invalid move
            throw new InvalidMoveException("Invalid move!");
        }
        game.makeMove(move);



        //verifies validity of the move

        //
        return null;
    }

    public String leaveGame(UserGameCommand command, Session session, ConcurrentHashMap<Integer, HashSet<Session>> connections){
        return null;
    }

    public String resignGame(UserGameCommand command, Session session, ConcurrentHashMap<Integer, HashSet<Session>> connections){
        return null;
    }

    public String getUsername(UserGameCommand command) throws DataAccessException {
        try{
            return authDAO.getAuthData(command.getAuthToken()).username();
        }
        catch(DataAccessException e){
            throw new DataAccessException(401, "Username not found in database, invalid authToken, check @ getUserName in websocket service");
        }

    }




}
