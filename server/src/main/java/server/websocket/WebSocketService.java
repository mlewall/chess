package server.websocket;
import chess.*;
import dataaccess.database.SqlAuthDao;
import dataaccess.database.SqlGameDao;
import dataaccess.database.SqlUserDao;
import model.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketException;
import websocket.commands.MoveCommand;
import websocket.commands.UserGameCommand;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import dataaccess.*;
import websocket.messages.LoadGameMessage;
import websocket.messages.ServerMessage;


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

    //client sends server a connect message (contains commandType, gameID, authToken)
    //we don't need to worry about adding players here because they've already been added via http
    public ServerMessage connect(UserGameCommand command, Session session, ConcurrentHashMap<Integer, HashSet<Session>> connections) throws DataAccessException {
        //get Username from dao (based on authToken)
        AuthData authData = authDAO.getAuthData(command.getAuthToken());
        if(authData == null){
            throw new DataAccessException(401, "Username not found in database; invalid authToken");
        }

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

    public ServerMessage makeMove(MoveCommand command, Session session,
                           ConcurrentHashMap<Integer, HashSet<Session>> connections) throws DataAccessException, InvalidMoveException {
        //1) validate user (this could be factored out)
        String username = authDAO.getAuthData(command.getAuthToken()).username();
        if(username == null){
            throw new DataAccessException("Username not found in database; invalid authToken. ");
        }
        //2) get gameData associated with ID
        GameData oldGameData = gameDAO.getGame(command.getGameID());
        if(oldGameData == null){
            throw new DataAccessException("Game not found in database; invalid gameID. ");
        }
        //3) get ChessGame itself and check if it's over.
        ChessGame oldGame = oldGameData.game();
        if(oldGame.isOver){
            throw new DataAccessException("Game is over. No more moves can be made. ");
        }
        //4) validate whether this player can make a move
        String teamColor = getTeamColor(command);
        if(teamColor.equals("WHITE") && oldGame.getTeamTurn().equals(ChessGame.TeamColor.BLACK)){
            throw new DataAccessException("Can't make move for the black team.");
        }
        else if(teamColor.equals("BLACK") && oldGame.getTeamTurn().equals(ChessGame.TeamColor.WHITE)){
            throw new DataAccessException("Can't make move for the white team.");
        }

        //5) create a copy of the oldGame (for updating purposes)
        ChessGame newGame = new ChessGame(oldGame);

        //6) get move and validate it
        ChessMove move = command.getChessMove();
        Collection<ChessMove> possMoves = newGame.validMoves(move.getStartPosition());
        if(!possMoves.contains(move)){ //user requested an invalid move
            throw new InvalidMoveException("Invalid move!");
        }

        //7) make move, update. Make game message
        newGame.makeMove(move); //this InvalidMoveException error should be caught in the catch of onMessage
        GameData newGameData = new GameData(oldGameData.gameID(), oldGameData.whiteUsername(),
                oldGameData.blackUsername(), oldGameData.gameName(), newGame);
        gameDAO.updateGame(oldGameData, newGameData);
        LoadGameMessage gameMessage = new LoadGameMessage(LOAD_GAME, newGame);
        return gameMessage;
    }

    public void leaveGame(UserGameCommand command, Session session, ConcurrentHashMap<Integer, HashSet<Session>> connections) throws DataAccessException{
        AuthData authData = authDAO.getAuthData(command.getAuthToken());
        if(authData == null){
            throw new DataAccessException(401, "Username not found in database; invalid authToken");
        }

        //remove user from gameData (make a new gameData and update game)
        GameData ogGameData = gameDAO.getGame(command.getGameID());
        if(ogGameData == null){
            throw new DataAccessException("Game not found in database; invalid gameID");
        }

        //determine which team color they were
        String teamColor = getTeamColor(command);
        String whiteUsername = ogGameData.whiteUsername();
        String blackUsername = ogGameData.blackUsername();
        if(teamColor == "WHITE"){
            whiteUsername = null;
        }
        else if(teamColor == "BLACK"){
            blackUsername = null;
        }
        //make the new game data and overwrite
        GameData newGameData = new GameData(ogGameData.gameID(), whiteUsername, blackUsername, ogGameData.gameName(), ogGameData.game());
        gameDAO.updateGame(ogGameData, newGameData);

        // Remove their session from the connections hashset?
        HashSet<Session> sessions = connections.get(command.getGameID());
        if(sessions != null && !sessions.isEmpty()){
            sessions.remove(session);
        }

    }

    public void resignGame(UserGameCommand command, Session session, ConcurrentHashMap<Integer, HashSet<Session>> connections) throws DataAccessException{
        //both players and observers need to be able to resign.
        AuthData authData = authDAO.getAuthData(command.getAuthToken());
        if(authData == null){
            throw new DataAccessException("Username not found in database; invalid authToken");
        }

        GameData oldGameData = gameDAO.getGame(command.getGameID());
        if(oldGameData == null){
            throw new DataAccessException("Game not found in database; invalid gameID");
        }

        ChessGame game = oldGameData.game();
        game.isOver = true; //code in makeMoves will prevent this from happening
        GameData completedGame = new GameData(oldGameData.gameID(), oldGameData.whiteUsername(), oldGameData.blackUsername(), oldGameData.gameName(), game);
        gameDAO.updateGame(oldGameData, completedGame);

    }

    public String getUsername(UserGameCommand command) throws DataAccessException {
        try{
            return authDAO.getAuthData(command.getAuthToken()).username();
        }
        catch(DataAccessException e){
            throw new DataAccessException("Username not found in database, invalid authToken, check @ getUserName in websocket service");
        }

    }

    public String getTeamColor(UserGameCommand command) throws DataAccessException{
        int gameID = command.getGameID();
        String username = getUsername(command);
        GameData game = gameDAO.getGame(gameID);
        if(game.whiteUsername() != null && game.whiteUsername().equals(username)){
            return "WHITE";
        }
        else if(game.blackUsername() != null && game.blackUsername().equals(username)){
            return "BLACK";
        }
        else{
            return null;
        }
    }

    public GameEndStatus determineStaleCheckMate(ChessGame game){
        //now the OTHER team's move. Because after the move was made it toggles the turn(?)
        GameEndStatus status = new GameEndStatus();
        ChessGame.TeamColor otherTeamColor = game.getTeamTurn(); //it has been toggled
        if(game.isInCheck(otherTeamColor)){
            status.setInCheck(true);
        }
        if(game.isInCheckmate(otherTeamColor)){
            status.setInCheckMate(true);
            game.isOver = true;
        }
        if(game.isInStalemate(otherTeamColor)){
            status.setInStaleMate(true);
            game.isOver = true;
        }
        return status;
    }




}
