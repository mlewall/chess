package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessBoard;

public class GameplayRepl {
    ChessGame currentGame;
    String playerColor;
    ChessBoard board;

    GameplayRepl(ChessGame currentGame, String playerColor) {
        this.currentGame = currentGame;
        this.playerColor = playerColor;
        this.board = currentGame.getBoard();
    }

    public void run(){
        printBoard(currentGame, "WHITE");
        printBoard(currentGame, "BLACK");
    }

    public void printBoard(ChessGame currentGame, String playerColor){
        String board = null;
        if(playerColor.equalsIgnoreCase("BLACK")){
            constructBoard(currentGame, playerColor);
        }
        else if(playerColor.equalsIgnoreCase("WHITE")){
            constructBoard(currentGame, playerColor);
        }
        else{
            System.out.println("Error printing board");
        }
        //System.out.print(board);
    }

    public void constructBoard(ChessGame currentGame, String playerColor){
        StringBuilder sb = new StringBuilder();
        sb.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);

        sb.append(currentGame.getBoard().boardToString());

        sb.append(EscapeSequences.RESET_BG_COLOR);
        sb.append(EscapeSequences.RESET_TEXT_COLOR);
        System.out.print(sb);
    }

}
