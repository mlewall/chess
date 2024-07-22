package chess;

//import the collection and arraylist
import java.util.Collection;
import java.util.ArrayList;

//the "implements" here indicates we're following the model of the interface "PieceMovesCalculator"
public class KingMovesCalculator extends AbstractPieceMovesCalculator {

    public Collection<ChessMove> possPieceMoves(ChessBoard board, ChessPosition ogPosition){
        Collection<ChessMove> moves = new ArrayList<>();

        //indicate starting position (makes things easier)
        int currRow = ogPosition.getRow();
        int currColumn = ogPosition.getColumn();

        //these are all the possible moves; store it in an arraylist (hence why we imported the arraylist)
        //obv it's an arraylist because it's a curly-braced list of arrays

        //NOTE this syntax!!!
        int[][] possReach = {
                {1, -1},{1, 0},{1, 1},{0, -1}, {0, 1}, {-1, -1},{-1, 0},{-1, 1}
        }; //this represents maximum possible moves

        for(int[] move : possReach){
            int newRow = currRow + move[0]; //creates the new row we could move to (takes the first number from the first ArrayList (1))
            int newCol = currColumn + move[1]; //creates the new column we could move to (-1)
            ChessPosition endPos = new ChessPosition(newRow, newCol);
            //blocked normally takes blocked(ChessBoard board, ChessPosition possPosition, ChessGame.TeamColor teamcolor
            if(inBounds(newRow, newCol) && !blocked(board, ogPosition, endPos)){
                ChessMove oneMove = new ChessMove(ogPosition, endPos, null);
                //System.out.printf(oneMove.toString());
                moves.add(oneMove);
            }
        }
        return moves;
    }
    private boolean blocked(ChessBoard board, ChessPosition startPos, ChessPosition endPos){
        ChessPiece pieceAtDest = board.getPiece(endPos);
        ChessPiece currPiece = board.getPiece(startPos);

        if(pieceAtDest == null){
            return false;
        }
        else if(pieceAtDest.getTeamColor() == currPiece.getTeamColor()){
            return true;
        }
        else if(pieceAtDest.getTeamColor() != currPiece.getTeamColor()){
            return false;
        };
        return false;
    }


}
