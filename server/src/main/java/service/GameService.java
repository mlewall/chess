package service;

import dataaccess.GameDAO;
import dataaccess.AuthDAO;
import reqres.ListGamesRequest;
import reqres.ServiceResult;

public class GameService {
    public final GameDAO gameDAO;
    public final AuthDAO authDAO;
    //contains methods for register, login, logout
    //pass in the DAO generated at the server level (?)
    public GameService(GameDAO gameDAO, AuthDAO authDAO){
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public ServiceResult listGames(ListGamesRequest r){
        return null;
    }
}
