package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    //a pointer to a new 8x8 chessboard. Why did we call it chesspiece? Woudl it be better to call it Chessboard?
    public ChessPiece[][] squares = new ChessPiece[8][8];

    public ChessBoard() {}

    public ChessBoard(ChessBoard original){
        this.squares = new ChessPiece[8][8]; //create a new array, don't just use the one from initial declaration
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(original.squares[i][j] != null){
                    this.squares[i][j] = original.squares[i][j].copy();
                    //loop over the array and add a NEW copy of a chessPIece to each spot

                }
                else{
                    this.squares[i][j] = null;
                }
            }
        }
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        //System.out.println("Row" + position.getRow() + "Column" + position.getColumn());
        int row = position.getRow() - 1;
        int column = position.getColumn() - 1;
        squares[row][column] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        if (position.getRow() <= 8 && position.getRow() >= 1 && position.getColumn() <= 8 && position.getColumn() >= 1) {
            return squares[position.getRow() - 1][position.getColumn() - 1];
        } else {
            throw new RuntimeException("Given index out of bounds; check @ChessPiece.getPiece");
        }
    }

    public ChessPiece[][] getBoard(){
        return squares;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        //do I need to add letters?//make a graphical representation?

        //length should always be 8
        //set everything in the board to null
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                squares[i][j] = null;
            }
        }

        addPiece(new ChessPosition(1, 1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(1, 2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1, 4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(1, 5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(1, 6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1, 7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        //add pawns
        for (int i = 1; i <= 8; i++) {
            addPiece(new ChessPosition(2, i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        }

        addPiece(new ChessPosition(8, 1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(8, 2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(8, 5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(8, 6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));

        for (int i = 1; i <= 8; i++) {
            addPiece(new ChessPosition(7, i), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }
        //System.out.printf(boardToString());

    }

    @Override
    public String toString() {
        return boardToString();
    }

    public String boardToString() {
        StringBuilder sb = new StringBuilder();
        for (int row = 7; row >= 0; row--) {
            for (int column = 7; column >= 0; column--) {
                ChessPiece piece = squares[row][column]; //get the piece at that spot on the board
                if (piece == null) {
                    sb.append("- ");
                } else {
                    //it's a piece of specific type, look up the type, determine the letter
                    String symbol = piece.toString();
                    if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
                        sb.append(symbol.toLowerCase());
                    } else if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        sb.append(symbol.toUpperCase());
                    }
                    sb.append(" ");
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}