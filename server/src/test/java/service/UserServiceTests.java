package service;
import com.google.gson.Gson;
import dataaccess.*;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryUserDAO;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reqres.*;
import reqres.ServiceResult;

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
        authDAO.insertFakeAuth();
    }

    @Test
    //@DisplayName("Valid Username and Password")
    public void validLogin() throws DataAccessException{
        setUp();
        LoginRequest loginRequest = new LoginRequest("embopgirl", "chomp");
        ServiceResult result = userService.login(loginRequest);

        assert result instanceof LoginResult; //did we actually get a loginResult and not an exception
        assert ((LoginResult) result).username().equals("embopgirl"); //found the correct username in the db
        assert ((LoginResult) result).authToken() != null; //authToken was generated
        }

    //valid username/password combo
    @Test
    //@DisplayName("invalid Password")
    public void invalidPassword() throws DataAccessException{
        setUp();
        LoginRequest loginRequest = new LoginRequest("embopgirl", "incorrectPassword");

        DataAccessException ex = assertThrows(DataAccessException.class, () -> userService.login(loginRequest));

        assertEquals(401, ex.getStatusCode());
        assertEquals("Error: Unauthorized", ex.getMessage());
    }


    @Test
    public void invalidUsername() throws DataAccessException{
        setUp();
        LoginRequest loginRequest = new LoginRequest("nonexistentUser", "chomp");

        DataAccessException ex = assertThrows(DataAccessException.class, () -> userService.login(loginRequest));

        assertEquals(401, ex.getStatusCode());
        assertEquals("Error: Unauthorized", ex.getMessage());
    }
}





    //valid username, invalid password

