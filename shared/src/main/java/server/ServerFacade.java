package server;

import com.google.gson.Gson;
import model.SimplifiedGameData;
import reqres.*;

import java.io.*;
import java.net.*;


public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    //todo: make the response classes not null
    // String method, String path, Object request, Class<T> response)
    public void clear() throws ResponseException{
        String urlPath = "/db";
        this.makeRequest("DELETE", urlPath, null, null);
    }

    /*User methods*/
    public void register(String username, String password, String email) throws ResponseException{
        String urlPath = "/user";
        RegisterRequest registerRequest = new RegisterRequest(username, password, email);
        this.makeRequest("POST", urlPath, registerRequest, RegisterResult.class);
    }

    public void login(String username, String password) throws ResponseException{
        String urlPath = "/session";
        LoginRequest loginRequest = new LoginRequest(username, password);
        this.makeRequest("POST", urlPath, loginRequest, LoginResult.class);
    }

    public void logout(String authToken) throws ResponseException{
        String urlPath = "/session";
        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        this.makeRequest("DELETE", urlPath, logoutRequest, null);
    }

    /*game methods*/
    public void listGames(String authToken) throws ResponseException{
        String urlPath = "/game";
        record listGamesResult(SimplifiedGameData[] games){};
        this.makeRequest("GET", urlPath, null, null);
    }
    public void createGame() throws ResponseException{
        String urlPath = "/game";
        this.makeRequest("POST", urlPath, null, null);
    }
    public void joinGame() throws ResponseException{
        String urlPath = "/game";
        this.makeRequest("PUT", urlPath, null, null);
    }


    /*These methods were copied from petshop*/
    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
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

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
