package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private ChessGame.TeamColor teamColor;
    private  ChessPiece.PieceType pieceType;
    private PieceMovesCalculator movesCalculator; //this will be populated with a type-specific calculator

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.teamColor = pieceColor;
        this.pieceType = type;
        this.movesCalculator = determinePieceMovesCalculator(type); //determine move calculator based on piece type
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return teamColor == that.teamColor && pieceType == that.pieceType && Objects.equals(movesCalculator, that.movesCalculator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, pieceType, movesCalculator);
    }

    public PieceMovesCalculator determinePieceMovesCalculator(ChessPiece.PieceType type) {
        //this is only done once per piece. Assigns the correct calculator for given piece
        switch (type) {
            case KING: return new KingMovesCalculator();
            case QUEEN: return new  QueenMovesCalculator();
            case BISHOP: return new BishopMovesCalculator();
            case KNIGHT: return new KnightMovesCalculator();
            case ROOK: return new RookMovesCalculator();
            case PAWN: return new PawnMovesCalculator();
            default: throw new IllegalArgumentException("piece of unknown type at switch statement");
        }
        //throw new RuntimeException("Not fully implemented");
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */



    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        //movesCalculator is a piece-specific function, set up during Piece obj construction

        return movesCalculator.possPieceMoves(board, myPosition);

        //this returns a Collection of ChessMove objects
        //This represents all valid moves for a piece at a given position on the board

//        return new ArrayList<>();
        //throw new RuntimeException("Not implemented");
    }
}
