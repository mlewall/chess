package server;
import com.google.gson.JsonObject;
import dataaccess.*;
import dataaccess.database.*;
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
    boolean isInitialized = false;

    public Server() {
        //instantiate local variable DAOs & pass them to services
        try {
            UserDAO users = new SQLuserDAO();
            AuthDAO auths = new SQLauthDAO();
            GameDAO games = new SQLgameDAO();

            this.userService = new UserService(users, auths);
            this.gameService = new GameService(games, auths);
            this.clearService = new ClearService(users, auths, games);

            this.isInitialized = true;
        }
        catch(DataAccessException e) {
            //I did this because the test cases don't want the Server() constructor to throw an error
            //Also, in general constructors can't throw errors.
            System.err.println("Failed to initialize Server: " + e.getMessage());
            this.isInitialized = false;
        }
    }

    public int run(int desiredPort) {
        if(!isInitialized) {
            System.err.println("Error in server initialization. Cannot run.");
            return -1;
        }

        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        //or do DAOs get initialized here?
        //dependency injection

        //endpoints and exceptions
        Spark.delete("/db", this::clearHandler); //delete database

        Spark.post("/user", this::registerHandler); //register user
        Spark.post("/session", this::loginHandler); //login
        Spark.delete("/session", this::logoutHandler); //logout

        Spark.get("/game", this::listHandler); //listGames
        Spark.post("/game", this::createGameHandler); //createGame
        Spark.put("/game", this::joinHandler); //joinGame

        Spark.exception(DataAccessException.class, this::exceptionHandler);

        //global exception handler -- if something unanticipated went wrong
        Spark.exception(Exception.class, (exception, request, response) -> {
            response.status(500);
            response.body(new Gson().toJson(Map.of("message", "Error: Internal server error")));
        });

        //This line initializes the server and can be removed once you have a functioning endpoint 
        //Spark.init();

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
        them returning an Object, but I think that maybe String will be better because the JSON strings are ideally what
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
        //todo: maybe add checks to these handler methods that ensure the db was initialized?
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
        Gson gson =  new Gson();
        String authToken = request.headers("authorization");
        JsonObject content = gson.fromJson(request.body(), JsonObject.class);
        if(content.get("playerColor") == null || content.get("gameID")== null){
            ServiceResult result = new FailureResult("Error: bad request");
            response.status(400);
            return gson.toJson(result);
        }
        String playerColor = content.get("playerColor").getAsString();
        int gameID = content.get("gameID").getAsInt();

        JoinGameRequest joinRequest = new JoinGameRequest(authToken, playerColor, gameID);
        ServiceResult result = gameService.joinGame(joinRequest);
        return new Gson().toJson(result);
    }


}
