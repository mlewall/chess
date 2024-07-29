package service;


import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import org.mindrot.jbcrypt.BCrypt;
import reqres.*;
import dataaccess.UserDAO;
import model.*;
import reqres.ServiceResult;

import java.util.UUID;

/* Service objects take in a XRequest and return a XResponse */

public class UserService {
    public final UserDAO userDAO;
    public final AuthDAO authDAO;
    //contains methods for register, login, logout

    //pass in the DAO generated at the server level
    public UserService(UserDAO userDAO, AuthDAO authDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
//        try{
//            userDAO.insertFakeUser();
//        } //username is "fakeUsername", "fakePassword", "cheese.com"
//        catch(DataAccessException e) {
//            System.out.println("unable to add fake user on service setup");
//        }
    }

    public ServiceResult login(LoginRequest r) throws DataAccessException {
        if(r.username() == null || r.username().isBlank() || r.password() == null || r.password().isBlank()){
            throw new DataAccessException(400, "Error: bad request");
        }
        UserData singleUserData = userDAO.getUserData(r.username());
        //System.out.println("Password: " + singleUserData.password());
        if (singleUserData == null)  {
            throw new DataAccessException(401, "Error: unauthorized");
        }
        boolean passwordsMatch = BCrypt.checkpw(r.password(), singleUserData.password());
        if(!passwordsMatch){
            throw new DataAccessException(401, "Error: unauthorized");
        }
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, r.username());
        authDAO.addNewAuth(authData);
        return new LoginResult(r.username(), authToken); //a record that takes a username and authToken;
    }

    //comes in with an auth token
    public ServiceResult logout(LogoutRequest r) throws DataAccessException {
        String suppliedAuthToken = r.authToken();
        if (suppliedAuthToken == null || suppliedAuthToken.isBlank()) {
            throw new DataAccessException(400, "Error: bad request");
        }
        //had authToken, but it was wrong
        AuthData authData = authDAO.getAuthData(suppliedAuthToken);
        if (authData == null) { // we didn't find the authToken in there.
            throw new DataAccessException(401, "Error: unauthorized");
        }
        authDAO.remove(suppliedAuthToken);
        return new LogoutResult();

    }

    public ServiceResult register(RegisterRequest r) throws DataAccessException{
        if(r.username() == null || r.username().isBlank()
        || r.password() == null || r.password().isBlank()
        || r.email() == null || r.email().isBlank()){
            throw new DataAccessException(400, "Error: bad request");
        }

        UserData userData = new UserData(r.username(), r.password(), r.email());
        userDAO.insertNewUser(userData); //this will throw the error from the dataAccess class if there's a duplicate username

        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, r.username());
        authDAO.addNewAuth(authData);
        return new RegisterResult(r.username(), authToken);

        //figure out the 500 errors
    }

}
