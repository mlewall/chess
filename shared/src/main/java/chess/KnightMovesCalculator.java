package chess;

import javax.swing.text.Position;
import java.util.Collection;
import java.util.ArrayList;

public class KnightMovesCalculator implements PieceMovesCalculator {
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
