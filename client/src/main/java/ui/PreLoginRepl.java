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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PreLoginRepl implements NotificationHandler {
    private final ChessClient chessClient;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    //private boolean signedIn = false;
    //private String visitorName;

    public PreLoginRepl(String serverUrl) {
        chessClient = new ChessClient(serverUrl, this);
    }

    public void run(){
        System.out.println("Welcome to 240 Chess. Sign in or type \"help\" to get started.\n");
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        String result = "";
        while(!result.equals("quit")){
            System.out.print("\n" + "[LOGGED_OUT] " + ">>> ");
            String input = scanner.nextLine();

            try{
                result = eval(input);
                if(result.equals("quit")){
                    System.out.println("Goodbye!");
                    System.exit(0);
                }
                System.out.print(result);
                if(chessClient.signedIn){
                    PostLoginRepl postLoginRepl = new PostLoginRepl(chessClient);
                    postLoginRepl.run();
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + "Welcome back to the main menu." + EscapeSequences.RESET_TEXT_COLOR);
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
            if(!validateEmail(email)){
                return "Please enter a valid email address.";
            };

            try {
                RegisterResult result = chessClient.server.register(username, password, email);
                //todo: what happens if the credentials are wrong?
                chessClient.visitorName = username;
                chessClient.signedIn = true;
                return String.format("You were successfully registered and logged in as: %s \n", username);
            }
            catch(ResponseException ex){
                if(ex.getStatusCode() == 403){
                    return "Username already taken.";
                }
                else if(ex.getStatusCode() == 401){
                    return "An account already exists with this email address. Try a different one.";
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
            try {
                LoginResult result = chessClient.server.login(username, password);
                chessClient.visitorName = username;
                chessClient.signedIn = true;
                return String.format("Login success! \n");
            }
            catch(ResponseException ex){
                if(ex.getStatusCode() == 401){
                    return "Incorrect login. Please try again. ";
                }
            }
        }
        throw new ResponseException(400, "Invalid username or password");
    }


    public static String help(){
        return EscapeSequences.SET_TEXT_COLOR_BLUE + "CHESS LOGIN MENU: \n" + EscapeSequences.RESET_TEXT_COLOR +
               """ 
               1) register <USERNAME> <PASSWORD> <EMAIL> - to create an account
               2) login <USERNAME> <PASSWORD> - to play chess
               3) quit - quit chess portal
               4) help - list possible commands
               """;
    }

    public static boolean validateEmail(String emailStr) throws ResponseException{
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.matches();}


}
