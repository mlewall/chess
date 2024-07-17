package service;


import dataaccess.AuthDAO;
import reqres.LoginRequest;
import reqres.LoginResult;
import dataaccess.memory.MemoryUserDAO;
import dataaccess.UserDAO;
import model.*;

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


    public LoginResult login(LoginRequest r) {
        //look up the username in the database
        //if there is a username:
            //if username matches password?
                //make an auth token (here in this class cuz we need it to return), add it to the db.

        String username = r.username();
        String password = r.password();
        String authToken = ""; //this will be filled in later

        UserData singleUserData = userDAO.getUserData(username);
        if (singleUserData == null) {
            //todo: then the user didn't exist in the system, throw some kind of error
            return null;
        }
        else{
            if (singleUserData.password().equals(password)) {
                //generate an authentication token
                authToken = UUID.randomUUID().toString();
            }
        }

        LoginResult rr = new LoginResult(username, authToken); //a record that takes a username and authToken
        return rr;
    }

    //public static RegisterResult register(RegisterRequest r) {}
}
