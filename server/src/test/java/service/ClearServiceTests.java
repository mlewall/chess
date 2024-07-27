package service;

import dataaccess.*;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.ClearService;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClearServiceTests {
    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;
    ClearService clearService;

    @BeforeEach
    public void setUp() {
        this.userDAO = new MemoryUserDAO();
        this.authDAO = new MemoryAuthDAO();
        this.gameDAO = new MemoryGameDAO();

        this.clearService = new ClearService(userDAO, authDAO, gameDAO);
    }

    @Test
    public void clearEmptyDataBase(){
        clearService.resetDatabases();
        try {
            assertTrue(userDAO.isEmpty());
            assertTrue(authDAO.isEmpty());
            assertTrue(gameDAO.isEmpty());
        }
        catch (Exception e){
            System.out.println("Test failed: Unable to clear empty databases");
        }
    }

    @Test
    public void clearNonEmptyDataBase(){
        try {
            userDAO.insertFakeUser();
            gameDAO.addManyFakeGames();
            authDAO.addFakeAuth();
            clearService.resetDatabases();
            assertTrue(userDAO.isEmpty());
            assertTrue(authDAO.isEmpty());
            assertTrue(gameDAO.isEmpty());
        }
        catch (Exception e){
            System.out.println("Test: Unable to clear non-empty databases");
        }

    }



    }

