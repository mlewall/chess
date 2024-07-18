package service;


import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import reqres.FailureResult;
import reqres.LoginRequest;
import reqres.LoginResult;
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

    public ServiceResult login(LoginRequest r) {
        String username = r.username();
        String password = r.password();
        String authToken = ""; //this will be filled in later

        try{
            UserData singleUserData = userDAO.getUserData(username);
            if (singleUserData.password().equals(password)) {
                //generate an authentication token
                authToken = UUID.randomUUID().toString();
            }
            else{ //passwords don't match
                FailureResult errorMsg = new FailureResult("Error: unauthorized");
                return errorMsg;
            }
        }
        catch (DataAccessException e) { //username was not found in the database.
            FailureResult errorMsg = new FailureResult(e.getMessage());
            return errorMsg;
        }
        LoginResult rr = new LoginResult(username, authToken); //a record that takes a username and authToken
        return rr;
    }

    //public static RegisterResult register(RegisterRequest r) {}
}
