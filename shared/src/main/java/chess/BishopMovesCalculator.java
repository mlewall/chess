package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator extends AbstractPieceMovesCalculator {
    public Collection<ChessMove> possPieceMoves(ChessBoard board, ChessPosition ogPosition){
        Collection<ChessMove> moves = new ArrayList<>();

        ChessGame.TeamColor teamcolor = board.getPiece(ogPosition).getTeamColor();

        //go up-right
        calculateMovesInDirection(moves, board, ogPosition, 1, 1, teamcolor);
        //go down-right
        calculateMovesInDirection(moves, board, ogPosition, -1, 1, teamcolor);
        //up-left
        calculateMovesInDirection(moves, board, ogPosition, 1, -1, teamcolor);
        //down-left
        calculateMovesInDirection(moves, board, ogPosition, -1, -1, teamcolor);

        return moves;
    }


}


