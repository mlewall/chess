package chess;

/*interfaces are created just like classes
They are typically placed in the same package as the classes
that will implement them.*/


import java.util.Collection;

public interface PieceMovesCalculator {
    Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position);
    //this is a single method
    //all implementing classes must provide
}
