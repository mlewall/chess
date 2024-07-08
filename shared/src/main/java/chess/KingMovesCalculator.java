package chess;

//import the collection and arraylist
import java.util.Collection;
import java.util.ArrayList;

//the "implements" here indicates we're following the model of the interface "PieceMovesCalculator"
public class KingMovesCalculator implements PieceMovesCalculator {


    public Collection<ChessMove> possPieceMoves(ChessBoard board, ChessPosition position){
        Collection<ChessMove> moves = new ArrayList<>();

        //indicate starting position (makes things easier)
        int curr_row = position.getRow();
        int curr_column = position.getColumn();

        //these are all the possible moves; store it in an arraylist (hence why we imported the arraylist)
        //obv it's an arraylist because it's a curly-braced list of arrays

        //NOTE this syntax!!!
        int[][] possReach = {
                {1, -1},{1, 0},{1, 1},{0, -1}, {0, 1}, {-1, -1},{-1, 0},{-1, 1}
        }; //this represents maximum possible moves

        for(int[] move : possReach){
            int new_row = curr_row + move[0]; //creates the new row we could move to (takes the first number from the first ArrayList (1))
            int new_col = curr_column + move[1]; //creates the new column we could move to (-1)
            ChessPosition endPos = new ChessPosition(new_row, new_col);
            if(inBounds(new_row, new_col) && !blocked(board, position, endPos) && !promotion(board, endPos)){
                ChessMove oneMove = new ChessMove(position, endPos, null);
                //System.out.printf(oneMove.toString());
                moves.add(oneMove);
            }
        }
        return moves;
    }
    private boolean blocked(ChessBoard board, ChessPosition startPos, ChessPosition endPos){
        //if there is a piece of the same color at that possible destination, return false
        //todo: add an inbounds check here!
        ChessPiece pieceAtDest = board.getPiece(endPos);
        ChessPiece currPiece = board.getPiece(startPos);

        if(pieceAtDest == null){
            return false;
        }
        else if(pieceAtDest.getTeamColor() == currPiece.getTeamColor()){
            return true;
        }
        else if(pieceAtDest.getTeamColor() != currPiece.getTeamColor()){
            //todo: add another condition for if king takes that piece?
            return false;
        };
        return false;
    }

    private boolean inBounds(int row, int col){
        //todo!!! remember the inclusivity here.
        if (row > 0 && row <= 8 && col > 0 && col <= 8){
            return true;
        }
        return false;
    }

    private boolean promotion(ChessBoard board, ChessPosition endPos){
        //the king will never be promoted? It's just pawns, right?
        return false;
    }
}
