package chess;

//import the collection and arraylist
import java.util.Collection;
import java.util.ArrayList;

//the "implements" here indicates we're following the model of the interface "PieceMovesCalculator"
public class KingMovesCalculator implements PieceMovesCalculator {
    public Collection<ChessMove> possPieceMoves(ChessBoard board, ChessPosition position){
        Collection<ChessMove> moves = new ArrayList<>();

        //Todo: calculate King's moves

        return moves;
    }
}
