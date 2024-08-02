package client;

import model.AuthData;
import org.junit.jupiter.api.*;
import server.ResponseException;
import server.Server;
import server.ServerFacade;
import reqres.*;

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
        LogoutResult result = facade.logout(authData.authToken());
        //todo: figure out how to test that the logout was successful?
        assertThrows(ResponseException.class, () -> facade.createGame(authData.authToken(), "Test Game"));
    }

    @Test
    void logoutFailure() throws ResponseException {
        RegisterResult authData = facade.register("player1", "password", "myFirstEmail.com");
        assertThrows(ResponseException.class, () -> facade.logout("badAuthtoken"));
    }

    @Test
    void loginSuccess() throws ResponseException {
        //not sure about this because the database would have been cleared so you'd have to register again
        facade.register("player1", "password", "myFirstEmail.com");
        LoginResult authData = facade.login("player1", "password");
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void loginFailure() throws ResponseException {
        RegisterResult authData = facade.register("player1", "password", "myFirstEmail.com");
        facade.logout(authData.authToken());
        assertThrows(ResponseException.class, () -> facade.login("player1", "incorrectPW"));

    }





}
