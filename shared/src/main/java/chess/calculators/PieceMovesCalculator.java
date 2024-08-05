package chess.calculators;

/*interfaces are created just like classes
They are typically placed in the same package as the classes
that will implement them.*/


import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

//Note: interface methods are implicitly abstract and public. This interface is implemented by an abstract class.
public interface PieceMovesCalculator {
    Collection<ChessMove> possPieceMoves(ChessBoard board, ChessPosition position);
    //this is a single method
    //all implementing classes MUST provide (through the abstract method)
}
