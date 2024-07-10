package chess;

import java.util.Collection;
import java.util.ArrayList;

public class RookMovesCalculator implements PieceMovesCalculator {
    public Collection<ChessMove> possPieceMoves(ChessBoard board, ChessPosition og_position){
        Collection<ChessMove> moves = new ArrayList<>(); //this will contain the final set of moves

        ChessGame.TeamColor teamcolor = board.getPiece(og_position).getTeamColor();

        calculateMovesInDirection(moves, board, og_position, 1, 0, teamcolor);
        calculateMovesInDirection(moves, board, og_position, -1, 0, teamcolor);
        calculateMovesInDirection(moves, board, og_position, 0, 1, teamcolor);
        calculateMovesInDirection(moves, board, og_position, 0, -1, teamcolor);
        return moves;
    }

    private void calculateMovesInDirection(Collection<ChessMove> moves, ChessBoard board, ChessPosition og_position, int rowIncrement, int colIncrement, ChessGame.TeamColor teamcolor){
        int row = og_position.getRow();
        int col = og_position.getColumn();
        while(inBounds(row+rowIncrement, col+colIncrement)){
            row += rowIncrement;
            col += colIncrement;
            ChessPosition new_pos = new ChessPosition(row, col);
            if (!blocked(board, new_pos, teamcolor)) {
                ChessMove oneMove = new ChessMove(og_position, new_pos, null);
                moves.add(oneMove);
                //if there's an enemy here, then we break.
                if(enemyEncounter(board, new_pos, teamcolor)){
                    break;
                }
            } else {
                //if it's ever blocked, we should break.
                break;
            }
        }

    }


    private boolean inBounds(int row, int col){
        //todo!!! remember the inclusivity here.
        if (row > 0 && row <= 8 && col > 0 && col <= 8){
            return true;
        }
        return false;
    }

    private boolean blocked(ChessBoard board, ChessPosition poss_position, ChessGame.TeamColor teamcolor){
        //if there is a piece of the same color at that possible destination, return false
        //todo: add an inbounds check here!
        ChessPiece pieceAtDest = board.getPiece(poss_position);
        if(pieceAtDest == null){
            return false;
        }
        else if(pieceAtDest.getTeamColor() == teamcolor){
            return true;
            //blocked by our own teammate! NOOOO!
        }
        else if(pieceAtDest.getTeamColor() != teamcolor){
            return false;
        };
        return false;
    }


    private boolean enemyEncounter(ChessBoard board, ChessPosition end, ChessGame.TeamColor teamcolor){
        ChessPiece pieceAtDest = board.getPiece(end);
        if(pieceAtDest != null && teamcolor != pieceAtDest.getTeamColor()){
            return true;
        }
        return false;
    }
}


