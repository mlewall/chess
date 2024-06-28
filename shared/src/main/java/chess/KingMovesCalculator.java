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

        //NOTE this syntax!!! (seems pretty new)
        int[][] possReach = {
                {1, -1},{1, 0},{1, 1},{0, -1}, {0, 1}, {-1, -1},{-1, 0},{-1, 1}
        }; //this represents maximum possible moves

        for(int[] move : possReach){
            int new_row = curr_row + move[0]; //creates the new row we could move to (takes the first number from the first ArrayList (1))
            int new_col = curr_column + move[1]; //creates the new column we could move to (-1)
            if(inBounds(new_row, new_col)){
                ChessPosition endPos = new ChessPosition(new_row, new_col);
                ChessMove oneMove = new ChessMove(position, endPos, null);
                moves.add(oneMove);
            }
        }



        /*
        NOTES: (Remember ChessMove)
        public ChessMove(ChessPosition startPosition, ChessPosition endPosition, ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;}
         */



//        moves.add();
        //Todo: calculate King's moves
        //the king can move one square in any given direction
        //given start position [row][column]
        //8 possible moves maximum
        //not out of bounds
        //consider promotion pieces


        //cannot move to a place that is occupied (if it moves there, it takes that piece)

        return moves;
    }

    private boolean inBounds(int row, int col){
        if (row >= 0 && row < 8 && col >= 0 && col < 8){
            return true;
        }
        return false;
    }
}
