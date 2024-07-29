package service;

import chess.ChessGame;
import dataaccess.*;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import model.AuthData;
import model.GameData;
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
        try {
            clearService.resetDatabases();
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
            UserData fakeUser = new UserData("fakeUsername", "fakePassword", "cheese.com");
            AuthData fakeAuth = new AuthData("fakeAuthToken", "fakeUsername");
            ChessGame fakeChessGame = new ChessGame();
            GameData fakeGame = new GameData(1234, null, null, "fakeChessGame",
                    fakeChessGame);

            userDAO.insertNewUser(fakeUser);
            gameDAO.addGame(1234, fakeGame);
            authDAO.addNewAuth(fakeAuth);
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

