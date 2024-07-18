package server;
import dataaccess.*;
import dataaccess.memory.*;
import org.eclipse.jetty.server.Authentication;
import reqres.*;
import com.google.gson.Gson;

import service.*;

import spark.*; //includes spark.Request and spark.Response

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

    //needs to return an object: spark's requirement
    private Object loginHandler(Request request, Response response) {
        var loginRequest = new Gson().fromJson(request.body(), LoginRequest.class); //what class is supposed to go here?
        ServiceResult result = userService.login(loginRequest);
        if(result instanceof LoginResult){
            response.status(200);
            return new Gson().toJson(result);
        }
        else if(result instanceof FailureResult){
            response.status(401);
            return new Gson().toJson(result);
        }
        //LoginRequest contains username, pw: UserData[username=embopgirl, password=yeehaw]
        return null;
    }

    private Object clearHandler(Request request, Response response) throws DataAccessException {
        var clearRequest = new Gson().fromJson(request.body(), LoginRequest.class); //what class is supposed to go here?
        ServiceResult result = clearService.resetDatabases();
        response.status(200);
        return new Gson().toJson(result);
    }
    private String registerHandler(Request request, Response response) {
        return null;
    }
    //put login back here
    private Object logoutHandler(Request request, Response response) {
        return null;
    }
    private Object listHandler(Request request, Response response) {
        return null;
    }
    private Object createGameHandler(Request request, Response response) {
        return null;
    }
    private Object joinHandler(Request request, Response response) {
        return null;
    }
}
