package service;
import dataaccess.*;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryUserDAO;
import org.junit.jupiter.api.Test;
import reqres.*;
import reqres.ServiceResult;

import static dataaccess.memory.MemoryAuthDAO.AUTHS;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTests {
    UserDAO userDAO;
    AuthDAO authDAO;
    UserService userService;

    public void setUp() {
        this.userDAO = new MemoryUserDAO();
        this.authDAO = new MemoryAuthDAO();
        this.userService = new UserService(userDAO, authDAO);

        userDAO.insertFakeUser();
        authDAO.addFakeAuth();
    }

    @Test
    //@DisplayName("Valid Username and Password")
    public void validLogin() throws DataAccessException{
        setUp();
        LoginRequest loginRequest = new LoginRequest("fakeUsername", "fakePassword");
        LoginResult result = (LoginResult) userService.login(loginRequest);

        assertEquals(result.username(),"fakeUsername"); //found the correct username in the db
        assertNotNull(result.authToken()); //authToken was generated
        }

    //valid username/password combo
    @Test
    //@DisplayName("correct username, invalid Password")
    public void invalidPassword() throws DataAccessException{
        setUp();
        LoginRequest loginRequest = new LoginRequest("fakeUsername", "incorrectPassword");

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
        RegisterRequest registerRequest = new RegisterRequest("fakeUsername", "elegy", "yahoo.com");
        DataAccessException ex = assertThrows(DataAccessException.class, () -> userService.register(registerRequest));
        assertEquals(403, ex.getStatusCode());
        assertEquals("Error: already taken", ex.getMessage());
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

