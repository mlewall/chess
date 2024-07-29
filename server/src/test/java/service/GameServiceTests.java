package service;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.*;
import dataaccess.database.SqlAuthDao;
import dataaccess.database.SqlGameDao;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reqres.*;
import model.GameData;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {
    //UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;
    GameService gameService;

    @BeforeEach
    public void setUp() {
        try {
        this.authDAO = new SqlAuthDao();
        this.gameDAO = new SqlGameDao();
        this.gameService = new GameService(gameDAO, authDAO);

        gameDAO.clear();
        authDAO.clear();

        AuthData fakeAuth = new AuthData("fakeAuthToken", "fakeUsername");
        authDAO.addNewAuth(fakeAuth); //"fakeAuthToken", "fakeUsername"
        }
        catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void addGames() throws DataAccessException {
        ChessGame fakeChessGame0 = new ChessGame();
        GameData game1 = new GameData(1, null, null, "fakeChessGame",
                fakeChessGame0);
        ChessGame fakeChessGame1 = new ChessGame();
        ChessGame fakeChessGame2 = new ChessGame();
        ChessGame fakeChessGame3 = new ChessGame();
        GameData game2 = new GameData(2, "white", "black",
                "fakeChessGame1", fakeChessGame1);
        GameData game3 = new GameData(3, "cloud", "ebony",
                "fakeChessGame2", fakeChessGame2);
        GameData game4 = new GameData(4, "snow", "yeet",
                "fakeChessGame3", fakeChessGame3);
        gameDAO.addGame(1, game1);
        gameDAO.addGame(2, game2);
        gameDAO.addGame(3, game3);
        gameDAO.addGame(4, game4);

    }

    public void addFakeGame() throws DataAccessException {
        ChessGame fakeChessGame = new ChessGame();
        GameData fakeGame = new GameData(1234, null, null, "fakeChessGame",
                fakeChessGame);
        gameDAO.addGame(1234, fakeGame);
    }

    @Test
    public void listGamesNoGamesOK() throws DataAccessException {
        ListGamesRequest request = new ListGamesRequest("fakeAuthToken");
        ListGamesResult result = (ListGamesResult) gameService.listGames(request);
        assertNotNull(result.games());
    }

    @Test
    public void listGamesOK() throws DataAccessException {
        ChessGame fakeChessGame = new ChessGame();
        GameData game1 = new GameData(1234, null, null, "fakeChessGame",
                fakeChessGame);
        gameDAO.addGame(1234, game1);
        ListGamesRequest request = new ListGamesRequest("fakeAuthToken");
        ListGamesResult result = (ListGamesResult) gameService.listGames(request);
        assertNotNull(result.games());
        assertEquals(1, result.games().size());
    }


    @Test
    public void listMultipleGamesOk() throws DataAccessException {
        addGames();
        ListGamesRequest request = new ListGamesRequest("fakeAuthToken");
        ListGamesResult result = (ListGamesResult) gameService.listGames(request);
        assertNotNull(result.games());
        assertEquals(result.games().size(), 4);
    }

    @Test
    public void listGamesBadAuth() throws DataAccessException {
        ListGamesRequest request = new ListGamesRequest("incorrectAuthToken");
        DataAccessException ex = assertThrows(DataAccessException.class, () -> gameService.listGames(request));

        assertEquals(401, ex.getStatusCode());
        assertEquals("Error: unauthorized", ex.getMessage());;
    }

    @Test
    public void createGameSuccess() throws DataAccessException {
        CreateGameRequest request = new CreateGameRequest("fakeAuthToken", "myFirstGame");
        int dbSizeBefore = gameDAO.getGames().size();
        CreateGameResult result = (CreateGameResult) gameService.createGame(request);
        int dbSizeAfter = gameDAO.getGames().size();

        assertEquals((dbSizeBefore + 1), dbSizeAfter);
        assertTrue(result.gameID() > 0); //gameIDs should be positive integers.

        GameData createdGame = gameDAO.getGame(result.gameID());
        assertNotNull(createdGame);
        assertEquals(createdGame.gameName(), request.gameName());
    }

    @Test
    public void createGameDuplicateGameNames() throws DataAccessException {
        CreateGameRequest request = new CreateGameRequest("fakeAuthToken", "game1");
        CreateGameResult result = (CreateGameResult) gameService.createGame(request);
        int dbSizeBefore = gameDAO.getGames().size();
        CreateGameRequest request2 = new CreateGameRequest("fakeAuthToken", "game1");
        CreateGameResult result2 = (CreateGameResult) gameService.createGame(request2);
        int dbSizeAfter = gameDAO.getGames().size();
        assertEquals((dbSizeBefore + 1), dbSizeAfter);
    }

    @Test
    public void createGameNullGameName() throws DataAccessException {
        CreateGameRequest request = new CreateGameRequest("fakeAuthToken", null);

        DataAccessException ex = assertThrows(DataAccessException.class, () -> gameService.createGame(request));

        assertEquals(400, ex.getStatusCode());
        assertEquals("Error: blank gameName", ex.getMessage());

    }

    @Test
    public void joinSuccess() throws DataAccessException {
        addFakeGame(); //adds: GameData(1234, null, null, "fakeChessGame",fakeChessGame)
        JoinGameRequest request = new JoinGameRequest("fakeAuthToken", "WHITE", 1234);
        JoinGameResult result = (JoinGameResult) gameService.joinGame(request);
        assertEquals(result.getStatusCode(), 200);
        GameData game = gameDAO.getGame(1234); //current game
        //(the values of the gameName and chessGame shouldn't have changed)
        assertEquals(game, new GameData(1234, "fakeUsername", null, game.gameName(), game.game()));
    }

    @Test
    public void failToJoinNonexistentGame() throws DataAccessException {
        addFakeGame();
        JoinGameRequest request = new JoinGameRequest("fakeAuthToken", "WHITE", 8008);
        DataAccessException ex = assertThrows(DataAccessException.class, () -> gameService.joinGame(request));
        assertEquals(400, ex.getStatusCode());
        assertTrue(ex.getMessage().matches("Error: .*"));
    }

    @Test
    public void joinBadAuthToken() throws DataAccessException {
        addFakeGame();
        JoinGameRequest request = new JoinGameRequest("badAuth", "BLACK", 1234);
        DataAccessException ex = assertThrows(DataAccessException.class, () -> gameService.joinGame(request));
        assertEquals(401, ex.getStatusCode());
        assertTrue(ex.getMessage().matches("Error: .*"));
    }

}
