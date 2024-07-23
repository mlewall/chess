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

    protected void loopOverMoveSet(ChessBoard board, ChessPosition ogPosition, ChessGame.TeamColor teamColor, int[][] possMoves, Collection<ChessMove> moves) {
        for(int[] move : possMoves) {
            int newRow = ogPosition.getRow() + move[0];
            int newColumn = ogPosition.getColumn() + move[1];
            ChessPosition newPos = new ChessPosition(newRow, newColumn);
            if(inBounds(newRow, newColumn)){
                if(!blocked(board, newPos, teamColor)){
                    ChessMove oneMove = new ChessMove(ogPosition, newPos, null);
                    moves.add(oneMove);
                }
            }
        }
    }

    protected void calculateMovesInDirection(Collection<ChessMove> moves, ChessBoard board, ChessPosition ogPosition,
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
