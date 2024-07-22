package chess;

import java.util.Collection;

public abstract class AbstractPieceMovesCalculator implements PieceMovesCalculator {
    public abstract Collection<ChessMove> possPieceMoves(ChessBoard board, ChessPosition position);

    /*these are methods that are common to lots of the implementing classes*/
    protected boolean inBounds(int row, int col){
        if (row > 0 && row <= 8 && col > 0 && col <= 8){
            return true;
        }
        return false;
    }

    protected boolean blocked(ChessBoard board, ChessPosition possPosition, ChessGame.TeamColor teamcolor){
        ChessPiece pieceAtDest = board.getPiece(possPosition);
        if(pieceAtDest == null){
            return false;
        }
        else if(pieceAtDest.getTeamColor() == teamcolor){ //blocked by own teammate
            return true;
        }
        else if(pieceAtDest.getTeamColor() != teamcolor){
            return false;
        };
        return false;
    }
    private boolean blocked(ChessBoard board, ChessPosition startPos, ChessPosition endPos){
        ChessPiece pieceAtDest = board.getPiece(endPos);
        ChessPiece currPiece = board.getPiece(startPos);

        if(pieceAtDest == null){
            return false;
        }
        else if(pieceAtDest.getTeamColor() == currPiece.getTeamColor()){
            return true;
        }
        else if(pieceAtDest.getTeamColor() != currPiece.getTeamColor()){
            return false;
        };
        return false;
    }



    protected boolean enemyEncounter(ChessBoard board, ChessPosition end, ChessGame.TeamColor teamcolor){
        ChessPiece pieceAtDest = board.getPiece(end);
        if(pieceAtDest != null && teamcolor != pieceAtDest.getTeamColor()){
            return true;
        }
        return false;
    }


}
