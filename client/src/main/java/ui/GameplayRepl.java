package ui;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessBoard;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import static ui.EscapeSequences.*;

public class GameplayRepl {
    ChessGame currentGame;
    String playerColor;
    ChessBoard board;
    //todo: add something associated with websockets? unless that's made somewhere else and passed in

    private static final String EMPTY = "   ";

    GameplayRepl(ChessGame currentGame, String playerColor) {
        this.currentGame = currentGame;
        this.playerColor = playerColor;
        this.board = currentGame.getBoard();
    }

    public void run(){
        //todo: switch commands here probably
        drawBoard();
    }

    public void drawBoard() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);

        //drawGrayBackground(out);
        drawLetterHeader(out, playerColor);
        //todo: determine if a board should really go here
        drawChessBoard(out, board, playerColor);
        drawLetterHeader(out, playerColor);

        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
    }


    private static void drawLetterHeader(PrintStream out, String playerColor) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
        //if white: if black, reverse this.
        String[] cols = new String[]{};
        if (playerColor.equals("WHITE")) {
            cols = new String[]{"a", "b", "c", "d", "e", "f", "g", "h"}; //white order
        }
        else if (playerColor.equals("BLACK")) {
            cols = new String[]{"h", "g", "f", "e", "d", "c", "b", "a"};
        }
        out.print(EMPTY);
        for(String row : cols){
            out.print(" "+ row + " ");
        }
        out.print(EMPTY);
        out.println();
    }

    //blue is black: h-a, 8-1
    //red is white: a-h, 1-8
    private static void drawChessBoard(PrintStream out, ChessBoard board, String playerColor) {
        String[] numRowLabels = getRowLabels(playerColor);
        //int row_itr = 0;
        //this draws all the individual horizontal rows
        ChessPiece[][] currBoard = board.getBoard(); //same for every
        for(int boardRow = 0; boardRow < 8; boardRow++) { //boardRow counts from top of the VISUAL disp. to bottom
            ChessPiece[] rowWithPieces = null;
            if(playerColor.equals("WHITE")) { //if we're showing the white perspective (white at bottom)
                rowWithPieces = currBoard[7 - boardRow]; //grabs the BLACK pieces FIRST (from bottom of ChessBoard)
            }
            else if(playerColor.equals("BLACK")) { //black needs to be at bottom of visual
                rowWithPieces = currBoard[boardRow]; //get the first row from currBoard (it's white)
                rowWithPieces = reverseRow(rowWithPieces); //flip it around

            }
            out.print(SET_TEXT_COLOR_BLACK);
            out.print(SET_BG_COLOR_LIGHT_GREY);
            assert numRowLabels != null;
            out.print(numRowLabels[boardRow]);

            drawBoardRow(out, boardRow, rowWithPieces, playerColor);

            out.print(SET_BG_COLOR_LIGHT_GREY);
            out.print(SET_TEXT_COLOR_BLACK);
            out.print(numRowLabels[boardRow]);
            //row_itr ++;
            out.println(); //newline once the row is done?
        }
    }

    private static String[] getRowLabels(String playerColor) {
        if (playerColor.equals("WHITE")) {
            return new String[]{" 8 ", " 7 ", " 6 ", " 5 ", " 4 ", " 3 ", " 2 ", " 1 "};
        }
        else if (playerColor.equals("BLACK")) {
            return new String[]{" 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 "};
        }
        return null;
    }

    private static void drawBoardRow(PrintStream out, int rowInd, ChessPiece[] rowWithPieces, String playerColor) {
        for(int col = 0; col < 8 ; col++) { //goes across
            //make checkers
            if((col+rowInd) % 2 == 0){
                out.print(SET_BG_COLOR_WHITE);
                //call placePieces here
                placePiece(out, rowWithPieces, col, playerColor);
                out.print(RESET_TEXT_COLOR);
                out.print(RESET_BG_COLOR);
            }
            else{
                out.print(SET_BG_COLOR_BLACK);
                placePiece(out, rowWithPieces, col, playerColor);
                out.print(RESET_BG_COLOR);
                out.print(RESET_TEXT_COLOR);
            }
        }
    }

    private static void placePiece( PrintStream out, ChessPiece[] rowWithPieces, int col, String playerColor) {
        if(rowWithPieces[col] != null){
            //if it's black you need to need to iterate BACKWARDS through the row
            ChessPiece piece = rowWithPieces[col]; //this is where we must specify the correct piece!
            if(piece.getTeamColor().equals(ChessGame.TeamColor.WHITE)){
                out.print(SET_TEXT_COLOR_RED);
            }
            else{
                out.print(SET_TEXT_COLOR_BLUE);
            }
            out.print(" " + rowWithPieces[col].toString() + " ");
        }
        else {
            out.print(EMPTY);
        }
    }

    private static ChessPiece[] reverseRow(ChessPiece[] row){
        ChessPiece[] reversed = new ChessPiece[row.length];
        for(int i = 0; i < row.length; i++){
            reversed[i] = row[row.length - 1 - i];
        }
        return reversed;
        }
    }

