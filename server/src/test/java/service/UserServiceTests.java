package service;
import dataaccess.*;
import dataaccess.database.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import reqres.*;
import reqres.ServiceResult;

import static dataaccess.memory.MemoryAuthDAO.AUTHS;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTests {
    static UserDAO userDAO;
    static AuthDAO authDAO;
    static UserService userService;


    public void setUp() {
        try{
        userDAO = new SqlUserDao();
        authDAO = new SqlAuthDao();
        userService = new UserService(userDAO, authDAO);

        userDAO.clear();
        authDAO.clear();

        //baseline registration; this is for tests that need a password
        RegisterRequest registerRequest = new RegisterRequest("TestUsername", "TestPassword", "TestEmail");
        userService.register(registerRequest);

        UserData fakeUser = new UserData("fakeUsername", "fakePassword", "cheese.com");
        AuthData fakeAuth = new AuthData("fakeAuthToken", "fakeUsername");

        userDAO.insertNewUser(fakeUser); //UserData fake = new UserData("fakeUsername", "fakePassword", "cheese.com");
        authDAO.addNewAuth(fakeAuth); //AuthData fakeAuth = new AuthData("fakeAuthToken", "fakeUsername");
        }
        catch (Exception e) {
            System.out.println("Unable to register fake user in database for testing");
        }
    }

    @AfterAll
    public static void tearDown() {
        try{
            userDAO.clear();
            authDAO.clear();
        }
        catch (Exception e) {
            System.out.println("Unable to clear database after all tests");
        }

    }


    @Test
    //@DisplayName("Valid Username and Password")
    public void validLogin() throws DataAccessException{
        setUp();

        LoginRequest loginRequest = new LoginRequest("TestUsername", "TestPassword");
        LoginResult result = (LoginResult) userService.login(loginRequest);

        assertEquals(result.username(),"TestUsername"); //found the correct username in the db
        assertNotNull(result.authToken()); //authToken was generated
        }

    //valid username/password combo
    @Test
    //@DisplayName("correct username, invalid Password")
    public void invalidPassword() throws DataAccessException{
        setUp();
        LoginRequest loginRequest = new LoginRequest("TestUsername", "incorrectPassword");

        DataAccessException ex = assertThrows(DataAccessException.class, () -> userService.login(loginRequest));

        assertEquals(401, ex.getStatusCode());
        assertEquals("Error: unauthorized", ex.getMessage());
    }


    @Test
    public void invalidUsername() throws DataAccessException{
        setUp();
        LoginRequest loginRequest = new LoginRequest("nonexistentUser", "fakePassword");

        DataAccessException ex = assertThrows(DataAccessException.class, () -> userService.login(loginRequest));

        assertEquals(401, ex.getStatusCode());
        assertEquals("Error: unauthorized", ex.getMessage());
    }

    @Test
    public void registerValidUser() throws DataAccessException{
        setUp();
        RegisterRequest registerRequest = new RegisterRequest("hillbilly", "elegy", "yahoo.com");
        RegisterResult result = (RegisterResult) userService.register(registerRequest);
        assertEquals(result.username(), "hillbilly");
        assertNotNull(result.authToken());
        //todo: add another check that ensures the authData is actually in the database?
    }

    @Test
    /*username already taken*/
    public void registerInvalidUser() throws DataAccessException{
        setUp();
        //duplicate username
        RegisterRequest registerRequest = new RegisterRequest("TestUsername", "elegy", "yahoo.com");
        DataAccessException ex = assertThrows(DataAccessException.class, () -> userService.register(registerRequest));
        assertEquals(403, ex.getStatusCode());
        assertEquals("Error: username already taken", ex.getMessage());
    }



    @Test
    public void logoutValidUser() throws DataAccessException{
        setUp(); // inserts fakeUser fakeUsername, fakePassword, fakeAuthToken
        LogoutRequest logoutRequest = new LogoutRequest("fakeAuthToken");
        ServiceResult result = userService.logout(logoutRequest);
        assertFalse(AUTHS.containsKey("fakeAuthToken")); //make sure it's deleted from the db
        //assert something else?
    }

    @Test
    public void logoutInvalidAuthToken() throws DataAccessException{
        setUp();
        LogoutRequest logoutRequest = new LogoutRequest("invalidAuthToken");
        DataAccessException ex = assertThrows(DataAccessException.class, () -> userService.logout(logoutRequest));

        assertEquals(401, ex.getStatusCode());
        assertEquals("Error: unauthorized", ex.getMessage());

    }


}





    //valid username, invalid password

