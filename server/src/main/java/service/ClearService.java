package service;

import dataaccess.*;
import reqres.ClearResult;

public class ClearService {
    public final UserDAO userDAO;
    public final AuthDAO authDAO;
    public final GameDAO gameDAO;
    //contains methods for register, login, logout
    //pass in the DAO generated at the server level (?)
    public ClearService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public ClearResult resetDatabases() {
        //userDAO.insertFakeUser();
        userDAO.clear();
        authDAO.clear();
        gameDAO.clear();
        //todo: figure out exactly where the errors are supposed to be handled here.
        if(!userDAO.isEmpty() && !authDAO.isEmpty() && !gameDAO.isEmpty()){
            return null;
        }
        return new ClearResult();

    }
}
