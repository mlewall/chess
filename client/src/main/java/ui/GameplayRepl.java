package ui;
import chess.*;
import client.ChessClient;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import org.glassfish.grizzly.http.server.Response;
import server.ResponseException;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GameplayRepl implements NotificationHandler {
    ChessGame currentGame;
    String playerColor;
    ChessClient chessClient;
    WebSocketFacade wsFacade;
    String authToken;
    int gameID;
    boolean playerLeaving;
    HashMap<String, Integer> columnsToNums;
    //todo: add something associated with websockets? unless that's made somewhere else and passed in

    private static final String EMPTY = "   ";

    GameplayRepl(String playerColor, ChessClient chessClient, int gameID) {
        this.playerColor = playerColor;
        this.chessClient = chessClient;
        authToken = chessClient.server.authToken; //just for ease of access
        this.gameID = gameID;
        playerLeaving = false;
        columnsToNums = convertColumns();

    }

    public void setWebSocketFacade(WebSocketFacade wsFacade) {
        this.wsFacade = wsFacade;
    }

    //these are not part of the loop, and thus will interrupt things sometimes. What to do about that?
    @Override
    public void updateGame(ChessGame game) {
        /* So this is drawn whenever the update is received */
        System.out.println("Received game update: " + game);  //DEBUG
        GameVisual boardDrawer = new GameVisual(game, this.playerColor);
        this.currentGame = game; //save a copy of game in case we want to draw board
        boardDrawer.drawBoard();
        System.out.print("\n" + "[IN_GAME] " + ">>> " );
    }

    @Override
    public void printMessage(String message) {
        System.out.println(message);
        System.out.print("\n" + "[IN_GAME] " + ">>> " );
    }

    public void run() {
        //todo: switch commands here probably to get input from the user.
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE +
                "Welcome to gameplay, @" + chessClient.visitorName + "!"
                + EscapeSequences.RESET_TEXT_COLOR)
        ;

        printHelp(); //print help

        Scanner scanner = new Scanner(System.in);
        //String result = "";
        while(chessClient.signedIn && !playerLeaving){
            System.out.print("\n" + "[IN_GAME] " + ">>> " );
            String input = scanner.nextLine();

            try{
                eval(input); //sometimes will this print out some kind of gameBoard?
                //System.out.println(result); //what is getting printed "null"?
            }
            catch(Exception e){
                var msg = e.toString();
                System.out.print(msg);
            }
        }
    }

    public void eval(String input){
        try{
            var tokens = input.split(" ");
            var command = (tokens.length > 0) ? tokens[0] : "help"; //gets rid of the first param (the command)
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
             switch(command){
                case "redraw" -> redrawBoard();
                case "leave" -> leaveGame();
                case "move" -> makeMove(params);
                case "resign" -> resignGame();
                case "highlight" -> highlightMoves(params);
                //case "clear" -> clear();

                case "quit" -> System.out.println("To abandon game, type in \"leave\". To resign, type \"resign\""); //you can choose to make them log out or can just quite
                default -> printHelp();
            };
        }
        catch(ResponseException e){
            //todo: find a better way to deal with errors.
            System.out.print( e.getMessage());
        }
    }

    public void redrawBoard(){
        GameVisual boardDrawer = new GameVisual(this.currentGame, this.playerColor);
        boardDrawer.drawBoard();

    }
    public void leaveGame() throws ResponseException {
        //removes the user from the game
        wsFacade.leaveGame(authToken, gameID);
        playerLeaving = true; //you'll go back to the loop, which will stop because playerLeaving is true now.
        System.out.print("Successfully left the game.");
        //sends a message
    }
    public void makeMove(String...params) throws ResponseException {
        //g2 g4 (src col src row, end col end row)
        if (params.length < 2) { //not enough args
            throw new ResponseException(400, "Specify in this format: move <start> <end> (e.g. move c4 f7)");
        }
        String originStr = params[0];
        String destinationStr = params[1];
//        Integer originColumn; //g
//        Integer originRow; //2
//        Integer destinationColumn; //g
//        Integer destinationRow; //4

        try{
            //these columns might be null
            Integer originColumn = this.columnsToNums.get(originStr.substring(0, 1)); //g
            Integer originRow = Integer.parseInt(originStr.substring(1, 2)); //2
            Integer destinationColumn = this.columnsToNums.get(destinationStr.substring(0, 1)); //g
            Integer destinationRow = Integer.parseInt(destinationStr.substring(1, 2)); //4
            if(originColumn != null && originRow != null && destinationColumn != null && destinationRow != null){
                ChessPosition origin = new ChessPosition(originRow, originColumn);
                ChessPosition destination = new ChessPosition(destinationRow, destinationColumn);
                //todo: determine what to do about promo pieces (get piece at destination? -- is this handled?)
                ChessMove move = new ChessMove(origin, destination, null);
                if(playerColor.equals(currentGame.getTeamTurn().toString())){
                    wsFacade.makeMove(authToken, gameID, move);
                }
                else{
                    System.out.println("Error: not your turn!\n"); //is it okay to just hardcode this message in here?
                }
            }
        }
        catch(NumberFormatException e){
            System.out.println("Invalid move input. Please specify in format: " +
                    "move c1 f4 (move <start> <end>");
        }


    }
    public void highlightMoves(String...params){
        //just one location (g4)
        //find valid moves
        //print all the squares with all squares with end positions
        //return "highlighted";
    }

    public void resignGame() throws ResponseException {
        //return "resigned";
        System.out.print("\n" + "Are you sure you want to resign? [yes]/[no] \n" );
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        if(input.equalsIgnoreCase("yes")){
            wsFacade.resignGame(authToken, gameID);
            currentGame.isOver = true;
            //this does NOT cause the user to leave the game.
            //do they exit the repl?
        }
        //todo: add something else here?
    }

    private void printHelp(){
        System.out.print("""
                OPTIONS: 
                redraw - redraw the chess board
                leave - leave the game (whether playing or observing, you will be removed from the game)
                move - input a move <in this format>
                resign - forfeit the game (you will still be in the game)
                highlight <piece> - will highlight all the moves for that piece
                """);
        //return help;
    }

    HashMap<String, Integer> convertColumns(){
        HashMap<String, Integer> columnsToNumbers = new HashMap<>();
        String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h"};
        int i = 1; //board number
            //chesspositions are 1-indexed
        for(String letter: letters){
            columnsToNumbers.put(letter, i); //a: 1, etc.
            i++;
        }
        return columnsToNumbers;
    }


}




