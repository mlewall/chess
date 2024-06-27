package chess;

/*interfaces are created just like classes
They are typically placed in the same package as the classes
that will implement them.*/


import java.util.Collection;

//Note: interface methods are implicitly abstract and public



public interface PieceMovesCalculator {
    Collection<ChessMove> possPieceMoves(ChessBoard board, ChessPosition position);
    //this is a single method
    //all implementing classes must provide
}
