package chess;

//import the collection and arraylist
import java.util.Collection;
import java.util.ArrayList;


public class KingMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position){
        Collection<ChessMove> moves = new ArrayList<>();

        //Todo: calculate King's moves

        return moves;
    }
}
