package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator implements PieceMovesCalculator {
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

    private void calculateMovesInDirection(Collection<ChessMove> moves, ChessBoard board, ChessPosition ogPosition, int rowIncrement, int colIncrement, ChessGame.TeamColor teamcolor){
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

    private boolean inBounds(int row, int col){
        //todo!!! remember the inclusivity here.
        if (row > 0 && row <= 8 && col > 0 && col <= 8){
            return true;
        }
        return false;
    }

    private boolean blocked(ChessBoard board, ChessPosition possPosition, ChessGame.TeamColor teamcolor){
        //if there is a piece of the same color at that possible destination, return false
        //todo: add an inbounds check here!
        ChessPiece pieceAtDest = board.getPiece(possPosition);
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


