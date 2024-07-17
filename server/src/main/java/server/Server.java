package server;
import com.google.gson.Gson;

import model.*;
import service.*;

import service.ReqRes.*;
import spark.*; //includes spark.Request and spark.Response

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

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
        //deserialize JSON (the request body)
        var loginRequest = new Gson().fromJson(request.body(), LoginRequest.class); //what class is supposed to go here?
        //model contains username, pw, and email: UserData[username=embopgirl, password=yeehaw, email=yurt.yahoo.com]

        //service call
        LoginResult username_authToken = UserService.login(loginRequest);

        return new Gson().toJson(username_authToken);
    }


    private String clearHandler(Request request, Response response) {
        return null;
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
