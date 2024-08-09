package ui;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessBoard;
import client.ChessClient;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import server.ResponseException;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GameplayRepl implements NotificationHandler {
    //ChessGame originalGame;
    String playerColor;
    ChessBoard board;
    GameVisual gameVisual;
    ChessClient chessClient;
    //WebSocketFacade wsFacade;
    //todo: add something associated with websockets? unless that's made somewhere else and passed in

    private static final String EMPTY = "   ";

    GameplayRepl(String playerColor, ChessClient chessClient) {
        //this.originalGame = originalGame;
        this.playerColor = playerColor;
        this.chessClient = chessClient;
        //this.wsFacade = ws;
        //this.board = currentGame.getBoard();
        //this.gameVisual = new GameVisual(currentGame, playerColor);
    }

    public void run() {
        //todo: switch commands here probably to get input from the user.
        System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE +
                "Welcome to gameplay, @" + chessClient.visitorName + "!"
                + EscapeSequences.RESET_TEXT_COLOR)
        ;
        //gameVisual.drawBoard();
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        String result = "";
        while(chessClient.signedIn && !result.equals("leave")){
            System.out.print("\n" + "[IN_GAME] " + ">>> ");
            String input = scanner.nextLine();

            try{
                result = eval(input); //sometimes will this print out some kind of gameBoard?
                System.out.println(result);
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
                case "redraw" -> redrawBoard();
                case "leave" -> leaveGame();
                case "move" -> makeMove();
                case "resign" -> resignGame();
                case "highlight" -> highlightMoves();
                //case "clear" -> clear();

                case "quit" -> "To quit, type in \"leave\"."; //you can choose to make them log out or can just quite
                default -> help();
            };
        }
        catch(Exception e){
            //todo: find a better way to deal with errors.
            return e.getMessage();
        }
    }

    public String redrawBoard(){
        //returns a string (board)
        return "board";
    }
    public String leaveGame(){
        //sends a message
        return "left";
    }
    public String makeMove() {
        return "moved";
    }
    public String highlightMoves(){
        return "highlighted";
    }
    public String resignGame(){
        return "resigned";
    }


    private String help(){
        String help = """
                OPTIONS: 
                redraw - redraw the chess board
                leave - leave the game (whether playing or observing, you will be removed from the game)
                move - input a move <in this format>
                resign - forfeit the game (you will still be in the game)
                highlight <piece> - will highlight all the moves for that piece
                """;
        return help;
    }

    @Override
    public void updateGame(ChessGame game) {
        System.out.println("Received game update: " + game);  //DEBUG
        GameVisual boardDrawer = new GameVisual(game, this.playerColor);
        boardDrawer.drawBoard();
    }

    @Override
    public void printMessage(String message) {

    }
}




