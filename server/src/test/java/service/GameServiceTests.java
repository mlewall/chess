package service;

import dataaccess.*;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryGameDAO;
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
        this.authDAO = new MemoryAuthDAO();
        this.gameDAO = new MemoryGameDAO();
        this.gameService = new GameService(gameDAO, authDAO);

        gameDAO.clear();
        authDAO.clear();

        authDAO.addFakeAuth(); //"fakeAuthToken", "fakeUsername"
    }

    @Test
    public void listGamesNoGamesOK() throws DataAccessException {
        ListGamesRequest request = new ListGamesRequest("fakeAuthToken");
        ListGamesResult result = (ListGamesResult) gameService.listGames(request);
        assertNotNull(result.games());
    }

    @Test
    public void listGamesOK() throws DataAccessException {
        gameDAO.addFakeGame();
        ListGamesRequest request = new ListGamesRequest("fakeAuthToken");
        ListGamesResult result = (ListGamesResult) gameService.listGames(request);
        assertNotNull(result.games());
        assertEquals(1, result.games().size());
    }


    @Test
    public void listMultipleGamesOk() throws DataAccessException {
        gameDAO.addFakeGame();
        gameDAO.addManyFakeGames();
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
        gameDAO.addFakeGame(); //adds: GameData(1234, null, null, "fakeChessGame",fakeChessGame)
        JoinGameRequest request = new JoinGameRequest("fakeAuthToken", "WHITE", 1234);
        JoinGameResult result = (JoinGameResult) gameService.joinGame(request);
        assertEquals(result.getStatusCode(), 200);
        GameData game = gameDAO.getGame(1234); //current game
        //(the values of the gameName and chessGame shouldn't have changed)
        assertEquals(game, new GameData(1234, "fakeUsername", null, game.gameName(), game.game()));
    }

    @Test
    public void failToJoinNonexistentGame() throws DataAccessException {
        gameDAO.addFakeGame();
        JoinGameRequest request = new JoinGameRequest("fakeAuthToken", "WHITE", 8008);
        DataAccessException ex = assertThrows(DataAccessException.class, () -> gameService.joinGame(request));
        assertEquals(400, ex.getStatusCode());
        assertTrue(ex.getMessage().matches("Error: .*"));
    }

    @Test
    public void joinBadAuthToken() throws DataAccessException {
        gameDAO.addFakeGame();
        JoinGameRequest request = new JoinGameRequest("badAuth", "BLACK", 1234);
        DataAccessException ex = assertThrows(DataAccessException.class, () -> gameService.joinGame(request));
        assertEquals(401, ex.getStatusCode());
        assertTrue(ex.getMessage().matches("Error: .*"));
    }



}
