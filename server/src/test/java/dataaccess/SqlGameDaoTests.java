package dataaccess;

import chess.ChessGame;
import dataaccess.*;
import dataaccess.database.SqlAuthDao;
import dataaccess.database.SqlGameDao;
import model.SimplifiedGameData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import reqres.*;
import model.GameData;
import service.GameService;

import java.util.ArrayList;

public class SqlGameDaoTests {
    static GameDAO gameDAO;


    @BeforeAll
    public static void setUp() {
        try{
            gameDAO = new SqlGameDao();
            gameDAO.clear();
        }
        catch (Exception e) {
            System.out.println("Unable to set up gameDAO for testing (check: setUp");
        }
    }

    @AfterEach
    public void reset() throws DataAccessException {
        gameDAO.clear();
    }


    @AfterAll
    public static void tearDown() throws DataAccessException {
        gameDAO.clear();
    }

    //clear
    @Test
    public void clear() throws DataAccessException {
        ChessGame fakeChessGame0 = new ChessGame();
        GameData game1 = new GameData(1, null, null, "fakeChessGame",
                fakeChessGame0);
        gameDAO.addGame(1, game1);
        assertFalse(gameDAO.isEmpty());
        gameDAO.clear();
        assertTrue(gameDAO.isEmpty());
    }

    @Test
    public void getGames() throws DataAccessException {
        ChessGame fakeChessGame1 = new ChessGame();
        ChessGame fakeChessGame2 = new ChessGame();
        GameData game1 = new GameData(1, null, null,
                "fakeChessGame", fakeChessGame1);
        GameData game2 = new GameData(2, null, null,
                "myGame", fakeChessGame2);
        gameDAO.addGame(1, game1);
        gameDAO.addGame(2, game2);
        assertFalse(gameDAO.getGames().isEmpty()); //should contain the two games
    }

    @Test
    public void getGameNoGames() throws DataAccessException {
        ArrayList<SimplifiedGameData> games = gameDAO.getGames();
        assertTrue(games.isEmpty());
        assertNotNull(games);
    }

    @Test
    public void getGameValid() throws DataAccessException {
        ChessGame fakeChessGame0 = new ChessGame();
        GameData game1 = new GameData(1, null, null,
                "fakeChessGame", fakeChessGame0);
        SimplifiedGameData game1simp = new SimplifiedGameData(1,
                null, null, "fakeChessGame");
        gameDAO.addGame(1, game1);
        GameData game = gameDAO.getGame(1);
        assertNotNull(game);
        assertTrue(game.gameName().equals("fakeChessGame"));
        }

    @Test
    public void getGameInvalid() throws DataAccessException {
        ChessGame fakeChessGame0 = new ChessGame();
        GameData game1 = new GameData(1, null, null,
                "fakeChessGame", fakeChessGame0);
        gameDAO.addGame(1, game1);
        assertNull(gameDAO.getGame(1789));
        //should return a null value if the ID isn't found in there
    }

    @Test
    public void addGameSuccess() throws DataAccessException {
        ChessGame fakeChessGame1 = new ChessGame();
        GameData game1 = new GameData(1, null, null,
                "fakeChessGame", fakeChessGame1);
        SimplifiedGameData game1simp = new SimplifiedGameData(1,
                null, null, "fakeChessGame");
        gameDAO.addGame(1, game1);
        assertTrue(gameDAO.getGames().contains(game1simp));

    }

    @Test
    //tries to add a duplicate (same gameID)
    public void addGameInvalid() throws DataAccessException {
        ChessGame fakeChessGame1 = new ChessGame();
        GameData game1 = new GameData(1, null, null,
                "fakeChessGame", fakeChessGame1);
        gameDAO.addGame(1, game1);
        ChessGame fakeChessGame2 = new ChessGame();
        GameData game2 = new GameData(1, null, null,
                "myGame", fakeChessGame2);
        DataAccessException ex = assertThrows(DataAccessException.class, () -> gameDAO.addGame(1, game2));
        assertTrue(ex.getMessage().contains("duplicate gameId"));
        assertEquals(403, ex.getStatusCode());
    }

    @Test
    public void updateGameSuccess() throws DataAccessException {
        ChessGame fakeChessGame1 = new ChessGame();
        GameData game1 = new GameData(1, null, null,
                "fakeChessGame", fakeChessGame1);
        gameDAO.addGame(1, game1);
        GameData updatedGame = new GameData(1, "newWhite", "newBlack", "fakeChessGame", fakeChessGame1);
        gameDAO.updateGame(game1, updatedGame);
        assertEquals("newWhite", gameDAO.getGame(1).whiteUsername());
        assertEquals("newBlack", gameDAO.getGame(1).blackUsername());

    }

    @Test
    //bad ID
    public void updateGameInvalid() throws DataAccessException {
        ChessGame fakeChessGame1 = new ChessGame();
        GameData game1 = new GameData(1, null, null,
                "fakeChessGame", fakeChessGame1);
        gameDAO.addGame(1, game1);
        GameData updatedGame = new GameData(9, "newWhite", "newBlack", "fakeChessGame", fakeChessGame1);
        assertThrows(DataAccessException.class, () ->  gameDAO.updateGame(game1, updatedGame));
    }

    @Test
    //is it okay if there's nothing to change
    public void updateGameNoUpdate() throws DataAccessException {
        ChessGame fakeChessGame1 = new ChessGame();
        GameData game1 = new GameData(1, null, null,
                "fakeChessGame", fakeChessGame1);
        gameDAO.addGame(1, game1);
        ChessGame fakeChessGame2 = new ChessGame();
        GameData updatedGame = new GameData(1, null, null,
                "fakeChessGame", fakeChessGame2);
        gameDAO.updateGame(game1, updatedGame);
        assertEquals(game1.gameID(), gameDAO.getGame(1).gameID());
        assertEquals(game1.whiteUsername(), gameDAO.getGame(1).whiteUsername());
        assertEquals(game1.blackUsername(), gameDAO.getGame(1).blackUsername());
        assertEquals(game1.gameName(), gameDAO.getGame(1).gameName());
        //assertEquals(game1.game(), gameDAO.getGame(1).game());
    }

}
