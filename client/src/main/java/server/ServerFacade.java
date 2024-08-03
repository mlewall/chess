package server;

import com.google.gson.Gson;
import model.SimplifiedGameData;
import reqres.*;
import reqres.facade.Authorization;
import reqres.facade.CreateGameReqBody;
import reqres.facade.JoinGameReqBody;

import java.io.*;
import java.net.*;
import java.util.ArrayList;


public class ServerFacade {
    private final String serverUrl;
    private String authToken;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public ServerFacade(int port){
        this.serverUrl = "http://localhost:" + port;
    }


    //todo: make the response classes not null
    // String method, String path, Object request, Class<T> response)
    public void clear() throws ResponseException{
        String urlPath = "/db";
        this.makeRequest("DELETE", urlPath, null,
                null, ClearResult.class);
    }

    /*User methods*/
    public RegisterResult register(String username, String password, String email) throws ResponseException{
        String urlPath = "/user";
        RegisterRequest registerRequest = new RegisterRequest(username, password, email);
        RegisterResult result = this.makeRequest("POST", urlPath, registerRequest,
                null, RegisterResult.class);
        this.authToken = result.authToken();
        return result;
    }

    //todo: figure out if this needs to be changed to VOID
    public LoginResult login(String username, String password) throws ResponseException{
        String urlPath = "/session";
        LoginRequest loginRequest = new LoginRequest(username, password);
        LoginResult result = this.makeRequest("POST", urlPath, loginRequest,
                null, LoginResult.class);
        this.authToken = result.authToken();
        return result;
    }

    public LogoutResult logout() throws ResponseException{
        //todo: account for authToken in the header (only input)
        String urlPath = "/session";
        Authorization auth = new Authorization(this.authToken);
        LogoutResult result = this.makeRequest("DELETE", urlPath, null,
                auth, LogoutResult.class);
        this.authToken = null;
        return result;
    }

    /*game methods*/
    public ArrayList<SimplifiedGameData> listGames() throws ResponseException{
        String urlPath = "/game";
        //todo: account for authToken being in the header of the request
        Authorization auth = new Authorization(authToken);
        ListGamesResult gameList = this.makeRequest("GET", urlPath, null,
                auth, ListGamesResult.class);
        //that list is contained within the listGames result
        return gameList.games();
    }
    public CreateGameResult createGame(String gameName) throws ResponseException{
        String urlPath = "/game";
        Authorization auth = new Authorization(authToken); //todo: account fr authToken being in the header of the request
        CreateGameReqBody name = new CreateGameReqBody(gameName);
        CreateGameResult result = this.makeRequest("POST", urlPath, name, auth, CreateGameResult.class);
        return result;
    }


    public JoinGameResult joinGame(String playerColor, int gameID) throws ResponseException{
        String urlPath = "/game";
        //todo: header = String authToken
        Authorization auth = new Authorization(authToken);
        //todo: body = playercolor, gameID
        JoinGameReqBody joinGameReqBody = new JoinGameReqBody(playerColor, gameID);
        JoinGameResult result = this.makeRequest("PUT", urlPath, joinGameReqBody, auth, JoinGameResult.class);
        return result;
    }


    /*These methods were copied from petshop*/
    private <T> T makeRequest(String method, String path, Object requestBodyData, Authorization headerData, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true); //we intend to use the URL connection for output

            //todo: add something about writing something to headers:
            // things containing headers:
            if(headerData != null) {
                writeHeaders(headerData, http);
            }
            if(requestBodyData != null) {
                writeBody(requestBodyData, http);
            }
            http.connect();
            throwIfNotSuccessful(http);

            //because the response comes back in JSON...?
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeHeaders(Authorization authToken, HttpURLConnection http) throws IOException {
        http.setRequestProperty("authorization", authToken.authToken());
    }

    /*
    1) checks the objects is not null
    2) if there's a request object, it sets the content type header to application/json
    3) uses Gson to serialize the request object to JSON
    4) writes the JSON string to the request body
     */
    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "failure: " + status);
        }
    }

    /*
    * 1) initializes the response var to null. Placeholder filled with actual
    * response data if the method successfully parses the response body.
    * 2) Then we check if the response has a body. (length < 0???)
    * 3) read the response body. open IO stream and wrap in InStreamReader
    * 4) parse the json; deserializing it into an obj of specified class.
    *

     * */
    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null; //placeholder that will be filled with the actual resp if method
        //successfully parses the response body
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }

        //response is an object of type T specified by responseClass
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
