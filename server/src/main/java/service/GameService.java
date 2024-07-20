package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.AuthDAO;
import model.AuthData;
import model.GameData;
import model.SimplifiedGameData;
import reqres.*;

import java.util.ArrayList;
import java.util.Random;

public class GameService {
    public final GameDAO gameDAO;
    public final AuthDAO authDAO;
    //contains methods for register, login, logout
    //pass in the DAO generated at the server level (?)
    public GameService(GameDAO gameDAO, AuthDAO authDAO){
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ServiceResult listGames(ListGamesRequest r) throws DataAccessException {
        if(r.authToken() == null || r.authToken().isBlank()){
            throw new DataAccessException(400, "Error: bad request");
        }
        AuthData authData = authDAO.getAuthData(r.authToken());
        if(authData == null){
            //invalid authToken
            throw new DataAccessException(401, "Error: unauthorized");
        }
        ArrayList<SimplifiedGameData> allGames = gameDAO.getAllGames();
        ListGamesResult result = new ListGamesResult(allGames);
        return result;
    }

    public ServiceResult createGame(CreateGameRequest r) throws DataAccessException {
        if(r.authToken() == null || r.authToken().isBlank()){
            throw new DataAccessException(400, "Error: bad request");
        }
        AuthData authData = authDAO.getAuthData(r.authToken());
        //todo: do I need to consider validating the authorization every time?
        if(authData == null){
            throw new DataAccessException(401, "Error: unable to find auth data given authToken (wrong authToken)");
        }
        int newGameID = createGameID();
        //try again and again till you get a unique one (this shouldn't happen very often)
        while(gameDAO.getGame(newGameID) != null){
            newGameID = createGameID();
        }
        GameData newGame = new GameData(newGameID, "", "", r.gameName(), new ChessGame());
        gameDAO.addGame(newGameID, newGame);
        CreateGameResult result = new CreateGameResult(newGameID);
        return result;
    }

    //this may be overkill
    public int createGameID() {
        //returns a 6-digit ID
        Random random = new Random();
        long timestamp = System.currentTimeMillis() % 1000000; // gets the last 6 digits of timestamp
        int randomPart = random.nextInt(1000); // gets random 3-digit number
        return (int)(timestamp * 1000 + randomPart); //combine them and cast as int
    }

    public ServiceResult joinGame(JoinGameRequest r) throws DataAccessException {
        //joinGame takes authToken, playerColor, and a game ID
        if(r.authToken() == null || r.authToken().isBlank()){
            throw new DataAccessException(400, "Error: missing authToken");
        }
        if(r.gameID() <= 0){ //how to check effectively that a gameid was supplied?
            throw new DataAccessException(400, "Error: invalid or missing gameID");
        }
        if(r.gameColor() == null || r.gameColor().isBlank()){
            throw new DataAccessException(400, "Error: invalid team color");
        }

        AuthData authData = authDAO.getAuthData(r.authToken());
        //todo: do I need to consider validating the authorization every time?
        if(authData == null){
            throw new DataAccessException(401, "Error: unauthorized (bad authToken)");
        }


    }
}
