package client;

import dataaccess.DataAccessException;
import model.AuthData;
import model.SimplifiedGameData;
import org.junit.jupiter.api.*;
import server.ResponseException;
import server.Server;
import server.ServerFacade;
import reqres.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clearDatabase() throws ResponseException {
        facade.clear();
    }

    /*My unit tests*/
    @Test
    public void sampleTest() {
        assertTrue(true);
    }

    @Test
    void registerSuccess() throws ResponseException {
        RegisterResult authData = facade.register("player1",
                "password", "myFirstEmail.com");
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void registerFailure() throws ResponseException {
        assertThrows(ResponseException.class, () -> facade.register("player2",
                null, "myFirstEmail.com"));
    }

    @Test
    void logoutSuccess() throws ResponseException {
        RegisterResult authData = facade.register("player1", "password", "myFirstEmail.com");
        LogoutResult result = facade.logout();
        assertThrows(ResponseException.class, () -> facade.createGame( "Test Game"));
    }

    @Test
    @DisplayName("Multiple logouts")
    void logoutFailure() throws ResponseException {
        RegisterResult authData = facade.register("player1", "password", "myFirstEmail.com");
        facade.logout();
        assertThrows(ResponseException.class, () -> facade.logout());
    }

    @Test
    void loginSuccess() throws ResponseException {
        //not sure about this because the database would have been cleared so you'd have to register again
        facade.register("player1", "password", "myFirstEmail.com");
        LoginResult authData = facade.login("player1", "password");
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void multipleLoginsOK() throws ResponseException {
        /*there is just one facade instance in use at a time so how does this work? */
        //I feel like there are implications here so I should take another look at this
        facade.register("player1", "password", "myFirstEmail.com");
        //facade.logout();
        facade.register("player2", "password", "mySecondEmail.com");
        //facade.logout();
        LoginResult auth1 = facade.login("player1", "password");
        LoginResult auth2 = facade.login("player2", "password");
        assert(auth1.authToken().length() > 10);
        assert(auth2.authToken().length() > 10);
        assertNotEquals(auth1.authToken(), auth2.authToken());
    }

    @Test
    void loginFailure() throws ResponseException {
        RegisterResult authData = facade.register("player1", "password", "myFirstEmail.com");
        facade.logout();
        assertThrows(ResponseException.class, () -> facade.login("player1", "incorrectPW"));
    }

    @Test
    void listGamesSuccess() throws ResponseException {
        RegisterResult authData = facade.register("player1", "password", "myFirstEmail.com");
        String authToken = authData.authToken();
        facade.createGame("Test Game1");
        facade.createGame("Test Game2");
        facade.createGame("Test Game3");
        ArrayList<SimplifiedGameData> gameList = facade.listGames();
        assertInstanceOf(ArrayList.class, gameList);
        assertEquals(3, gameList.size());
    }

    @Test
    void listGamesNoGames() throws ResponseException {
        RegisterResult authData = facade.register("player1", "password", "myFirstEmail.com");
        String authToken = authData.authToken();
        assertEquals(0, facade.listGames().size());
    }

    @Test
    @DisplayName("logged out list games")
    void listGamesFailure() throws ResponseException {
        RegisterResult authData = facade.register("player1", "password", "myFirstEmail.com");
        facade.createGame( "Test Game1");
        facade.createGame("Test Game2");
        facade.logout();
        assertThrows(ResponseException.class, () -> facade.listGames());

    }

    @Test
    void createGameSuccess() throws ResponseException {
        RegisterResult authData = facade.register("player1", "password", "myFirstEmail.com");
        String authToken = authData.authToken();
        facade.createGame("Test Game1");
        assertEquals(1, facade.listGames().size());
        //if the game was successfully created, then it should show up in the list
    }

    @Test
    @DisplayName("create game when logged out")
    void createGameFailure() throws ResponseException {
        RegisterResult authData = facade.register("player1", "password", "myFirstEmail.com");
        facade.createGame("Test Game1");
        facade.logout();
        assertThrows(ResponseException.class, () -> facade.createGame("Test Game2"));
    }

    @Test
    void joinSuccess() throws ResponseException {
        RegisterResult res = facade.register("firstUser", "pw", "myEmail.com");
        facade.createGame("Test Game1");
        facade.logout();
        facade.login("firstUser", "pw");
        int gameID = facade.listGames().getFirst().gameID();
        //TODO: ensure input is uppercase BEFORE it gets here!
        facade.joinGame("WHITE", gameID);
        assertTrue((facade.listGames().getFirst().whiteUsername()).equals("firstUser"));
    }


    @Test
    void joinFailure() throws ResponseException {
        facade.register("firstUser", "pw", "myEmail.com");
        facade.createGame("Test Game1");
        facade.logout();
        facade.login("firstUser", "pw");
        int gameID = facade.listGames().getFirst().gameID();
        //todo: should also fail with "white" and "black"
        assertThrows(ResponseException.class, () -> facade.joinGame("yellow", gameID));
    }

    @Test
    void joinFailureNoExist() throws ResponseException {
        facade.register("firstUser", "pw", "myEmail.com");
        facade.logout();
        facade.login("firstUser", "pw");
        //todo: should also fail with "white" and "black"
        assertThrows(ResponseException.class, () -> facade.joinGame("white", 4214));
    }








}
