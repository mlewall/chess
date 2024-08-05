package chess.calculators;
import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;
import java.util.ArrayList;


public class QueenMovesCalculator extends AbstractPieceMovesCalculator {
    public Collection<ChessMove> possPieceMoves(ChessBoard board, ChessPosition ogPosition){
        Collection<ChessMove> moves = new ArrayList<>();

        ChessGame.TeamColor teamcolor = board.getPiece(ogPosition).getTeamColor();

        //-----------DIAGONALS-----------//
        calculateMovesInDirection(moves, board, ogPosition, 1, 1, teamcolor); //up-right
        calculateMovesInDirection(moves, board, ogPosition, -1, 1, teamcolor); //down-right
        calculateMovesInDirection(moves, board, ogPosition, 1, -1, teamcolor); //up-left
        calculateMovesInDirection(moves, board, ogPosition, -1, -1, teamcolor); //down-left

        //------------------LATERALS-------------//
        calculateMovesInDirection(moves, board, ogPosition, 1, 0, teamcolor);
        calculateMovesInDirection(moves, board, ogPosition, -1, 0, teamcolor);
        calculateMovesInDirection(moves, board, ogPosition, 0, 1, teamcolor);
        calculateMovesInDirection(moves, board, ogPosition, 0, -1, teamcolor);

        return moves;
    }


}

