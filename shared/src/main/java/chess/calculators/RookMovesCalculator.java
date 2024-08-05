package chess.calculators;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;
import java.util.ArrayList;

public class RookMovesCalculator extends AbstractPieceMovesCalculator {
    public Collection<ChessMove> possPieceMoves(ChessBoard board, ChessPosition ogPosition){
        Collection<ChessMove> moves = new ArrayList<>(); //this will contain the final set of moves

        ChessGame.TeamColor teamcolor = board.getPiece(ogPosition).getTeamColor();

        calculateMovesInDirection(moves, board, ogPosition, 1, 0, teamcolor);
        calculateMovesInDirection(moves, board, ogPosition, -1, 0, teamcolor);
        calculateMovesInDirection(moves, board, ogPosition, 0, 1, teamcolor);
        calculateMovesInDirection(moves, board, ogPosition, 0, -1, teamcolor);
        return moves;
    }


}


