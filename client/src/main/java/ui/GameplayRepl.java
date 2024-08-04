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

    private static final int BOARD_SIZE_IN_SQUARES = 8; //8x8 board
    private static final int TOTAL_SIZE = BOARD_SIZE_IN_SQUARES + 2; //height and width
    private static final int SQUARE_WIDTH = 3; //" K "
    private static final int SQUARE_HEIGHT = 2;

    private static final String EMPTY = "   ";

    GameplayRepl(ChessGame currentGame, String playerColor) {
        this.currentGame = currentGame;
        this.playerColor = playerColor;
        this.board = currentGame.getBoard();
    }

    public void run() {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        out.print(ERASE_SCREEN);

        //drawGrayBackground(out);
        drawLetterHeader(out);
        drawChessBoard(out);

        drawLetterHeader(out);

        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
    }

//    private static void drawGrayBackground(PrintStream out) {
//        setGray(out);
//
//        for (int row = 0; row < TOTAL_SIZE; row++) {
//            for(int col = 0; col < TOTAL_SIZE; col++) {
//                out.print(EMPTY);
//            }
//            out.println();
//        }
//    }

    private static void drawLetterHeader(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_BLACK);
        //if white: if black, reverse this.
        String[] cols = new String[]{"a", "b", "c", "d", "e", "f", "g", "h"};
        out.print(EMPTY);
        for(String row : cols){
            out.print(" "+ row + " ");
        }
        out.print(EMPTY);
        out.println();
    }

    //blue is black: h-a, 8-1
    //red is white: a-h, 1-8
    private static void drawChessBoard(PrintStream out) {
        //if white, we begin at
        //draws a row
        String[] rows = new String[]{" 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 "};
        String[] revRows = new String[]{" 8 ", " 7 ", " 6 ", " 5 ", " 4 ", " 3 ", " 2 ", " 1 "};
        int row_itr = 0;
        //this draws all the individual horizontal rows
        for(int boardRow = 0; boardRow < 8; boardRow++) {
            out.print(SET_TEXT_COLOR_BLACK);
            out.print(SET_BG_COLOR_LIGHT_GREY);
            out.print(revRows[boardRow]);

            drawBoardRow(out, boardRow);

            out.print(SET_BG_COLOR_LIGHT_GREY);
            out.print(SET_TEXT_COLOR_BLACK);
            out.print(revRows[boardRow]);
            row_itr ++;
            out.println(); //newline once the row is done?
        }
        //out.println();

    }

    private static void drawBoardRow(PrintStream out, int boardRow) {
        for(int col = 0; col < 8 ; col++) { //goes across
            if(boardRow % 2 == 0){
                out.print(SET_BG_COLOR_WHITE);
                out.print(EMPTY);
                out.print(RESET_BG_COLOR);
            }
            else{
                out.print(SET_BG_COLOR_BLACK);
                out.print(EMPTY);
                out.print(RESET_BG_COLOR);
            }
//            out.print(SET_TEXT_COLOR_BLUE);
//            out.print(" K ");
            //placeholder
            //out.println(); this makes it very tall
        }

    }

    private static void setGray(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_LIGHT_GREY);
    }

    private String getPieceString(ChessPiece piece) {
        if(piece == null) {
            return EMPTY;
        }
        String pieceChar = piece.toString();
        return " " + pieceChar + " ";
    }


//public void printBoard(ChessGame currentGame, String playerColor) {
//        String board = null;
//        if(playerColor.equalsIgnoreCase("BLACK")){
//            constructBoard(currentGame, playerColor);
//        }
//        else if(playerColor.equalsIgnoreCase("WHITE")){
//            constructBoard(currentGame, playerColor);
//        }
//        else{
//            System.out.println("Error printing board");
//        }
//        //System.out.print(board);
//    }

    }

