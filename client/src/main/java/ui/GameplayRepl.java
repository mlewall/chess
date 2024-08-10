package ui;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessBoard;
import client.ChessClient;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import org.glassfish.grizzly.http.server.Response;
import server.ResponseException;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GameplayRepl implements NotificationHandler {
    ChessGame currentGame;
    String playerColor;
    ChessClient chessClient;
    WebSocketFacade wsFacade;
    String authToken;
    int gameID;
    //todo: add something associated with websockets? unless that's made somewhere else and passed in

    private static final String EMPTY = "   ";

    GameplayRepl(String playerColor, ChessClient chessClient, int gameID) {
        this.playerColor = playerColor;
        this.chessClient = chessClient;
        authToken = chessClient.server.authToken; //just for ease of access
        this.gameID = gameID;

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
        //redrawBoard(); //this is null at this point, so does nothing.
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        String result = "";
        while(chessClient.signedIn && !result.equals("leave")){
            System.out.print("\n" + "[IN_GAME] " + ">>> " );
            String input = scanner.nextLine();

            try{
                result = eval(input); //sometimes will this print out some kind of gameBoard?
                System.out.println(result); //what is getting printed "null"?
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
                case "move" -> makeMove(params);
                case "resign" -> resignGame();
                case "highlight" -> highlightMoves();
                //case "clear" -> clear();

                case "quit" -> "To abandon game, type in \"leave\". To resign, type \"resign\""; //you can choose to make them log out or can just quite
                default -> help();
            };
        }
        catch(ResponseException e){
            //todo: find a better way to deal with errors.
            return e.getMessage();
        }
    }

    public String redrawBoard(){
        GameVisual boardDrawer = new GameVisual(this.currentGame, this.playerColor);
        boardDrawer.drawBoard();
        return "Board redrawn.";
    }
    public String leaveGame() throws ResponseException {
        //removes the user from the game
        wsFacade.leaveGame(authToken, gameID);
        //sends a message
        return "leave";
    }
    public String makeMove(String...params) {
        //g2 g4 (src col src row, end col end row)
        return "moved";
    }
    public String highlightMoves(){
        //just one location
        //find valid moves
        //print all the squares with all squares with end positions
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


}




