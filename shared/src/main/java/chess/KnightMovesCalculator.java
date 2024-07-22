package chess;

import javax.swing.text.Position;
import java.util.Collection;
import java.util.ArrayList;

public class KnightMovesCalculator extends AbstractPieceMovesCalculator {
    public Collection<ChessMove> possPieceMoves(ChessBoard board, ChessPosition ogPosition){
        Collection<ChessMove> moves = new ArrayList<>();

        ChessGame.TeamColor teamColor = board.getPiece(ogPosition).getTeamColor();

        int startRow = ogPosition.getRow();
        int startColumn = ogPosition.getColumn();

        int[][] possMoves = {
                {2,-1},{2,1},
                {1,-2},{1, 2},
                {-1,-2},{-2,-1},
                {-2,1},{-1,2}
                /*at any given time there are only 8 moves*/
        };
        for(int[] move : possMoves) {
            int newRow = startRow + move[0];
            int newColumn = startColumn + move[1];
            ChessPosition newPos = new ChessPosition(newRow, newColumn);
            if(inBounds(newRow, newColumn)){
                if(!blocked(board, newPos, teamColor)){
                    ChessMove oneMove = new ChessMove(ogPosition, newPos, null);
                    moves.add(oneMove);
                }
            }
        }
        return moves;
    }

}
