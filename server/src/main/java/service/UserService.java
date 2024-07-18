package service;


import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import reqres.*;
import dataaccess.memory.MemoryUserDAO;
import dataaccess.UserDAO;
import model.*;
import reqres.ServiceResult;

import java.util.UUID;

/* Service objects take in a XRequest and return a XResponse */

public class UserService {
    public final UserDAO userDAO;
    public final AuthDAO authDAO;
    //contains methods for register, login, logout
    //pass in the DAO generated at the server level (?)
    public UserService(UserDAO userDAO, AuthDAO authDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public ServiceResult login(LoginRequest r) throws DataAccessException {
        String username = r.username();
        String password = r.password();
        String authToken = ""; //this will be filled in later

        UserData singleUserData = userDAO.getUserData(username);
        if (singleUserData == null || !singleUserData.password().equals(password)) {
            throw new DataAccessException(401, "Error: Unauthorized");
        }
        authToken = UUID.randomUUID().toString();
        return new LoginResult(username, authToken); //a record that takes a username and authToken;
    }

    //comes in with an auth token
    public ServiceResult logout(LogoutRequest r) throws DataAccessException {
        return null;
    }

    //public static RegisterResult register(RegisterRequest r) {}
}
