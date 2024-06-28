package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator implements PieceMovesCalculator {
    public Collection<ChessMove> possPieceMoves(ChessBoard board, ChessPosition og_position){
        Collection<ChessMove> moves = new ArrayList<>();

        int start_row = og_position.getRow();
        int start_col = og_position.getColumn();

        ChessGame.TeamColor teamcolor = board.getPiece(og_position).getTeamColor();

        //go up-right
        int up_row = start_row;
        int up_col = start_col;
        while(inBounds(up_row+1, up_col+1)){
            up_row++; up_col++;
            ChessPosition new_pos = new ChessPosition(up_row, up_col);
            if(!blocked(board, new_pos, teamcolor)){
                ChessMove oneMove = new ChessMove(og_position, new_pos, null);
                moves.add(oneMove);
                if(enemyEncounter(board, new_pos, teamcolor)){
                    break;
                }
            }
            else{
                break;
            }
        }

        //go down-right
        int down_row = start_row;
        int right_col = start_col;
        while(inBounds(down_row-1, right_col+1)){
            down_row--; right_col++;
            ChessPosition new_pos = new ChessPosition(down_row, right_col);
            if(!blocked(board, new_pos, teamcolor)){
                ChessMove oneMove = new ChessMove(og_position, new_pos, null);
                moves.add(oneMove);
                if(enemyEncounter(board, new_pos, teamcolor)){
                    break;
                }
            }
            else{
                break;
            }
        }

        //up-left
        up_row = start_row;
        int left_col = start_col;
        while(inBounds(up_row+1, left_col-1)) {
            up_row++; left_col--;
            ChessPosition new_pos = new ChessPosition(up_row, left_col);
            if (!blocked(board, new_pos, teamcolor)) {
                ChessMove oneMove = new ChessMove(og_position, new_pos, null);
                moves.add(oneMove);
                if(enemyEncounter(board, new_pos, teamcolor)){
                    break;
                }
            } else {
                break;
            }
        }

        down_row = start_row;
        left_col = start_col;
        while(inBounds(down_row-1, left_col-1)){
            down_row --; left_col--;
            ChessPosition new_pos = new ChessPosition(down_row, left_col);
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
        return moves;
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

    private boolean promotion(ChessBoard board, ChessPosition endPos){
        //only Pawns get promoted
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


