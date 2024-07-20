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
        String authToken = r.authToken();
        AuthData authData = authDAO.getAuthData(authToken);
        if(authData == null){
            //invalid authToken
            throw new DataAccessException(401, "Error: unauthorized");
        }
        ArrayList<SimplifiedGameData> allGames = gameDAO.getAllGames();
        ListGamesResult result = new ListGamesResult(allGames);
        return result;
    }

    public ServiceResult createGame(CreateGameRequest r) throws DataAccessException {
        String authToken = r.authToken();
        AuthData authData = authDAO.getAuthData(authToken);
        if(authData == null){
            throw new DataAccessException(401, "Error: unauthorized");
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

    public int createGameID() {
        //returns a 6-digit ID
        Random random = new Random();
        long timestamp = System.currentTimeMillis() % 1000000; // gets the last 6 digits of timestamp
        int randomPart = random.nextInt(1000); // gets random 3-digit number
        return (int)(timestamp * 1000 + randomPart); //combine them and cast as int
    }
}
