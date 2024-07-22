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

    private void calculateMovesInDirection(Collection<ChessMove> moves, ChessBoard board, ChessPosition ogPosition,
                                           int rowIncrement, int colIncrement, ChessGame.TeamColor teamcolor){
        int row = ogPosition.getRow();
        int col = ogPosition.getColumn();
        while(inBounds(row + rowIncrement, col + colIncrement)){
            row += rowIncrement;
            col += colIncrement;
            ChessPosition newPos = new ChessPosition(row, col);
            if (!blocked(board, newPos, teamcolor)) {
                ChessMove oneMove = new ChessMove(ogPosition, newPos, null);
                moves.add(oneMove);
                //if there's an enemy here, then we break.
                if(enemyEncounter(board, newPos, teamcolor)){
                    break;
                }
            } else {
                //if it's ever blocked, we should break.
                break;
            }
        }
    }

}


