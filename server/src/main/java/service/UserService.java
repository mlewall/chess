package service;


import service.ReqRes.*;

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
        String authToken = "1234"; //this will be filled in later
        // UserData record: String username, String password, String email
        //AuthData record: String authToken, String username

        LoginResult rr = new LoginResult(username, authToken); //a record that takes a username and authToken
        return rr;
    }

    //public static RegisterResult register(RegisterRequest r) {}
}
