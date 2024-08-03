package client;

import client.websocket.NotificationHandler;
//import webSocketMessages.Notification;
import java.util.Scanner;
import static ui.EscapeSequences.*;

/* the things contained in here are:
            1) register <USERNAME> <PASSWORD> <EMAIL> - to create an account
            2) login <USERNAME> <PASSWORD> - to play chess
            3) quit - playing chess
            4) help - with possible commands
     */

public class PreLoginRepl implements NotificationHandler {
    private final ChessClient chessClient;

    public PreLoginRepl(String serverUrl) {
        chessClient = new ChessClient(serverUrl, this);
    }

    public void run(){
        System.out.println("Welcome to 240 Chess. Sign in or type \"help\" to get started.");
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        String result = "";
        while(!result.equals("quit")){
            printPrompt();
            String line = scanner.nextLine();

            try{
                result = chessClient.eval(line);
                //sometimes will this print out some kind of gameBoard?
                System.out.print(result);
            }
            catch(Exception e){
                var msg = e.toString();
                System.out.print(msg);
            }

        }
    }

    private void printPrompt() {
        System.out.print("\n" + ">>> ");
    }

    public static String help(){
        return """
               1) register <USERNAME> <PASSWORD> <EMAIL> - to create an account
               2) login <USERNAME> <PASSWORD> - to play chess
               3) quit - playing chess
               4) help - with possible commands
                """;
    }
}
