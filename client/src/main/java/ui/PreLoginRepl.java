package ui;

import client.ChessClient;
import client.websocket.NotificationHandler;
import reqres.LoginResult;
import reqres.RegisterResult;
import server.ResponseException;
import server.ServerFacade;
//import webSocketMessages.Notification;
import java.util.Arrays;
import java.util.Scanner;

/* the things contained in here are:
            1) register <USERNAME> <PASSWORD> <EMAIL> - to create an account
            2) login <USERNAME> <PASSWORD> - to play chess
            3) quit - playing chess
            4) help - with possible commands
     */

public class PreLoginRepl implements NotificationHandler {
    private final ChessClient chessClient;
    private boolean signedIn = false;
    private String visitorName;

    public PreLoginRepl(String serverUrl) {
        chessClient = new ChessClient(serverUrl, this);
//        server = new ServerFacade(serverUrl);
//        signedIn = false;
    }

    public void run(){
        System.out.println("Welcome to 240 Chess. Sign in or type \"help\" to get started.\n");
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        String result = "";
        while(!result.equals("quit")){
            System.out.print("\n" + ">>> ");
            String input = scanner.nextLine();

            try{
                result = eval(input);
                //sometimes will this print out some kind of gameBoard?
                if(result.equals("quit")){
                    System.out.println("Goodbye!");
                    System.exit(0);
                }
                System.out.print(result);
                if(signedIn){
                    PostLoginRepl postLoginRepl = new PostLoginRepl(chessClient);
                    postLoginRepl.run();
                    System.out.println("Welcome back to the main menu.\n");
                    System.out.print(help());
                }

            }
            catch(Exception e){
                var msg = e.toString();
                System.out.print(msg);
            }
        }
    }

    public String eval(String input){
        try{
            var tokens = input.split(" ");
            var command = (tokens.length > 0) ? tokens[0] : "help"; //gets rid of the first param (the command)
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch(command){
                case "register" -> registerUser(params);
                case "login" -> userLogin(params);
                case "quit" -> "quit"; //you can choose to make them log out or can just quite
                case "clear" -> clear();
                default -> help();
            };
        }
        catch(ResponseException ex){
            return ex.getMessage();
        }
    }
    public String clear() throws ResponseException{
        chessClient.server.clear();
        return "Server has been reset";
    }

    public String registerUser(String...params) throws ResponseException {
        String username;
        String password;
        String email;
        if(params.length > 2){
            username = params[0];
            password = params[1];
            email = params[2];
            try {
                RegisterResult result = chessClient.server.register(username, password, email); //todo: what happens if the credentials are wrong?
                this.visitorName = username;
                signedIn = true;
                return String.format("You were successfully registered and logged in as: %s \n", username);
            }
            catch(ResponseException ex){
                if(ex.getStatusCode() == 403){
                    return "Username already taken";
                }
            }
            //--specify specific errors/input problems
        }

        throw new ResponseException(400, "Expected <username> <password> <email>");
    }

    public String userLogin(String...params) throws ResponseException {
        String username = null;
        String password = null;
        if (params.length > 1) {
            username = params[0];
            password = params[1];
            LoginResult result = chessClient.server.login(username, password);
            this.visitorName = username;
            signedIn = true;
            return String.format("You are now signed in as %s.", visitorName);
        }
        throw new ResponseException(400, "Invalid username or password");
    }


    public static String help(){
        return """
               1) register <USERNAME> <PASSWORD> <EMAIL> - to create an account
               2) login <USERNAME> <PASSWORD> - to play chess
               3) quit - quit chess portal
               4) help - list possible commands
               """;
    }
}
