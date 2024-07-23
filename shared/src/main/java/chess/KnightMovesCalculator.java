package chess;

import javax.swing.text.Position;
import java.util.Collection;
import java.util.ArrayList;

public class KnightMovesCalculator extends AbstractPieceMovesCalculator {
    public Collection<ChessMove> possPieceMoves(ChessBoard board, ChessPosition ogPosition){
        Collection<ChessMove> moves = new ArrayList<>();

        ChessGame.TeamColor teamColor = board.getPiece(ogPosition).getTeamColor();

        int[][] possMoves = {
                {2,-1},{2,1},
                {1,-2},{1, 2},
                {-1,-2},{-2,-1},
                {-2,1},{-1,2}
                /*at any given time there are only 8 moves*/
        };

        loopOverMoveSet(board, ogPosition, teamColor, possMoves, moves);
        return moves;
    }

}
