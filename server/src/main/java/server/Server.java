package server;
import dataaccess.*;
import dataaccess.memory.*;
import org.eclipse.jetty.server.Authentication;
import reqres.*;
import com.google.gson.Gson;

import service.*;

import spark.*; //includes spark.Request and spark.Response

import java.util.Map;

public class Server {
    //todo: initialize the DAOs here as fields (so there is just one of each)
    UserService userService;
    GameService gameService;
    ClearService clearService;


    public Server(){
        //instantiate local variable DAOs & pass them to services
        UserDAO users = new MemoryUserDAO();
        AuthDAO auths = new MemoryAuthDAO();
        GameDAO games = new MemoryGameDAO();

        this.userService = new UserService(users, auths);
        this.gameService = new GameService(games, auths);
        this.clearService = new ClearService(users, auths, games);

        //OR instantiate services with
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        //or do DAOs get initialized here?
        //dependency injection

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::clearHandler); //delete database

        Spark.post("/user", this::registerHandler); //register user
        Spark.post("/session", this::loginHandler); //login
        Spark.delete("/session", this::logoutHandler); //logout

        Spark.get("/game", this::listHandler); //listGames
        Spark.post("/game", this::createGameHandler); //createGame
        Spark.put("/game", this::joinHandler); //joinGame

        Spark.exception(DataAccessException.class, this::exceptionHandler);

        //add another things for exceptions
        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    /* Each handler will do three main things:
        1) deserialize request
        2) call the correct service
        3) serialize the response
        //each one takes in a Spark request and response and returns a JSON string (they used to be initialized with
        them returning an Object but I think that maybe String will be better because the JSON strings are ideally what
        they all return.
    */
    private void exceptionHandler(DataAccessException ex, Request request, Response response) {
        //throw all the problems and handle them up to this server level
        //catch the exception and then set the status code, set response body
        response.status(ex.getStatusCode());
        response.body(new Gson().toJson(Map.of("message", ex.getMessage())));
    }

    //needs to return an object: spark's requirement
    private Object clearHandler(Request request, Response response) throws DataAccessException {
        ServiceResult result = clearService.resetDatabases();
        return new Gson().toJson(result);
    }
    private Object loginHandler(Request request, Response response) throws DataAccessException{
        var loginRequest = new Gson().fromJson(request.body(), LoginRequest.class); //what class is supposed to go here?
        ServiceResult result = userService.login(loginRequest);
        return new Gson().toJson(result);
    }
    private String registerHandler(Request request, Response response) throws DataAccessException {
        var registerRequest = new Gson().fromJson(request.body(), RegisterRequest.class); //what class is supposed to go here?
        ServiceResult result = userService.register(registerRequest);
        return new Gson().toJson(result);
    }
    private Object logoutHandler(Request request, Response response) throws DataAccessException {
        LogoutRequest logoutRequest = new LogoutRequest(request.headers("authorization"));
        ServiceResult result = userService.logout(logoutRequest);
        return new Gson().toJson(result);
    }
    private Object listHandler(Request request, Response response) throws DataAccessException {
        ListGamesRequest listRequest = new ListGamesRequest(request.headers("authorization"));
        ServiceResult result = gameService.listGames(listRequest);
        return new Gson().toJson(result);
    }
    private Object createGameHandler(Request request, Response response) throws DataAccessException {
        String authToken = request.headers("authorization");
        Map<String, String> jsonMap = new Gson().fromJson(request.body(), Map.class);
        String gameName = jsonMap.get("gameName");
        CreateGameRequest createRequest = new CreateGameRequest(authToken, gameName);
        ServiceResult result = gameService.createGame(createRequest);
        return new Gson().toJson(result);

    }
    private Object joinHandler(Request request, Response response) throws DataAccessException {
        return null;
    }
}
