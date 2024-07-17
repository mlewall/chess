package service;


import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import model.*;
import service.ReqRes.*;

import java.util.UUID;

/* Service objects take in a XRequest and return a XResponse */

public class UserService {
    //contains methods for register, login, logout
    public static LoginResult login(LoginRequest r) {
        //look up the username in the database
        //if there is a username:
            //if username matches password?
                //make an auth token (here in this class cuz we need it to return), add it to the db.

        String username = r.username();
        String password = r.password();
        String authToken = ""; //this will be filled in later

        UserDAO currLookup = new MemoryUserDAO(); //this will have to change for the server
        UserData user = currLookup.getUserData(username);
        if (user == null) {
            //then the user didn't exist in the system, throw some kind of error
            return null;
        }
        else{
            if (user.password().equals(password)) {
                //generate an authentication token
                authToken = UUID.randomUUID().toString();
            }
        }

        LoginResult rr = new LoginResult(username, authToken); //a record that takes a username and authToken
        return rr;
    }

    //public static RegisterResult register(RegisterRequest r) {}
}
