package chess;

//import the collection and arraylist
import java.util.Collection;
import java.util.ArrayList;

//the "implements" here indicates we're following the model of the interface "PieceMovesCalculator"
public class KingMovesCalculator extends AbstractPieceMovesCalculator {

    public Collection<ChessMove> possPieceMoves(ChessBoard board, ChessPosition ogPosition){
        Collection<ChessMove> moves = new ArrayList<>();

        ChessGame.TeamColor teamColor = board.getPiece(ogPosition).getTeamColor();

        //these are all the possible moves; store it in an arraylist (hence why we imported the arraylist)
        int[][] possReach = {
                {1, -1},{1, 0},{1, 1},{0, -1}, {0, 1}, {-1, -1},{-1, 0},{-1, 1}
        }; //this represents maximum possible moves

        loopOverMoveSet(board, ogPosition, teamColor, possReach, moves);
        return moves;
    }



}
