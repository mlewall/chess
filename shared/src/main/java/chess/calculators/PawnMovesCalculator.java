package chess.calculators;

import chess.*;

import java.util.Collection;
import java.util.ArrayList;

public class PawnMovesCalculator implements PieceMovesCalculator {
    public Collection<ChessMove> possPieceMoves(ChessBoard board, ChessPosition ogPosition){
        Collection<ChessMove> moves = new ArrayList<>();

        int startRow = ogPosition.getRow();
        int startColumn = ogPosition.getColumn();
        ChessGame.TeamColor teamColor = board.getPiece(ogPosition).getTeamColor(); //we should be good getting here, start is inBounds
        boolean startingMove = isStartingMove(startRow, teamColor);

        //add DOUBLES
        if(startingMove){
            addDoubleMoves(ogPosition, teamColor, board, moves);
        }

        //right and left are only added if you're taking a piece
        //pawns can only be captured diagonally! So consider
        //1) is forwards move possible (blocked if straight ahead and other team)
        //2) is diagonal capture possible (not possible if no enemy piece is there)
        int[][] whitePossibilities = {{1, -1}, {1, 0}, {1, 1}}; //diagonal up, directup, diagonal right
        int[][] blackPossibilities = {{-1, -1}, {-1, 0}, {-1, 1}};

        int [][] moveSet = new int[0][];
        if(teamColor == ChessGame.TeamColor.WHITE){
            moveSet = whitePossibilities;
        }
        else if(teamColor == ChessGame.TeamColor.BLACK){
            moveSet = blackPossibilities;
        }

        for (int[] possibility : moveSet) {
            int newRow = startRow + possibility[0];
            int newColumn = startColumn + possibility[1];
            ChessPosition possPosition = new ChessPosition(newRow, newColumn);
            if(inBounds(newRow, newColumn)) {
                if (((newColumn == startColumn) && notFrontBlocked(board, ogPosition, teamColor)) ||
                        ((newColumn != startColumn) && inBounds(newRow, newColumn) && enemyEncounter(board, possPosition, teamColor))) {
                    if (promotion(possPosition)) {
                        //if it's a promotion, loop through the enum and add those moves.
                        addPromotions(ogPosition, possPosition, moves);
                    }
                    //not a promotion but still a one-forward step ahead move. Not blocked still, still in bounds, still straight ahead
                    else {
                        ChessMove newMove = new ChessMove(ogPosition, possPosition, null);
                        moves.add(newMove);
                    }
                }
            }
        }
        return moves;
    }

    private void addPromotions(ChessPosition ogPosition, ChessPosition possPosition, Collection<ChessMove> moves){
        for (ChessPiece.PieceType type : ChessPiece.PieceType.values()) {
            //exclude PAWN and KING
            if ((type.toString() != "PAWN") && (type.toString() != "KING")) {
                ChessMove newMove = new ChessMove(ogPosition, possPosition, type);
                moves.add(newMove);
                //this should add all possible promotions to the possible moves
            }
        }
    }

    private boolean inBounds(int row, int col){
        //todo!!! remember the inclusivity here.
        //shared between classes
        if (row > 0 && row <= 8 && col > 0 && col <= 8){
            return true;
        }
        return false;
    }

    private boolean enemyEncounter(ChessBoard board, ChessPosition end, ChessGame.TeamColor teamcolor){
        ChessPiece pieceAtDest = board.getPiece(end);
        if(pieceAtDest != null && teamcolor != pieceAtDest.getTeamColor()){
            return true;
        }
        return false;
    }

    /*the correct color made it all the way across
        (White could never get to 1 and black could never get to 8)
        note! if you don't put the color as well, it still passes the tests.
        might still be a good idea to be explicit
         */
    private boolean promotion(ChessPosition endPos){
        if((endPos.getRow() == 8)){
            return true;
        }
        else if((endPos.getRow() == 1)){
            return true;
        }
        return false;
    }

    private boolean notFrontBlocked(ChessBoard board, ChessPosition ogPosition, ChessGame.TeamColor teamcolor){
        if(teamcolor == ChessGame.TeamColor.WHITE){
            ChessPosition front = new ChessPosition(ogPosition.getRow()+1, ogPosition.getColumn());
            ChessPiece pieceAtFront = board.getPiece(front);
            if(pieceAtFront != null){
                return false;
            }
        }
        else if(teamcolor == ChessGame.TeamColor.BLACK){
            ChessPosition front = new ChessPosition(ogPosition.getRow()-1, ogPosition.getColumn());
            ChessPiece pieceAtFront = board.getPiece(front);
            if(pieceAtFront != null){
                return false;
            }
        }
        return true;
        }

    private boolean notBlocked(ChessBoard board, ChessPosition possPosition, ChessGame.TeamColor teamcolor){
        //this blocked is only used to check for the double front move for the pawn (in its edited form)
        ChessPiece pieceAtDest = board.getPiece(possPosition);
        return pieceAtDest == null;
    }

    private boolean isStartingMove(int row, ChessGame.TeamColor teamColor){
        if((row == 2 && teamColor == ChessGame.TeamColor.WHITE)||
                (row == 7 && ChessGame.TeamColor.BLACK == teamColor)){
            return true;
        }
        return false;
    }

    private void addDoubleMoves(ChessPosition ogPosition,
                                ChessGame.TeamColor teamColor, ChessBoard board, Collection<ChessMove> moves){
        if(teamColor == ChessGame.TeamColor.WHITE){
            ChessPosition possPos = new ChessPosition(ogPosition.getRow()+2, ogPosition.getColumn());
            if(notBlocked(board, possPos, teamColor) && notFrontBlocked(board, ogPosition, teamColor)){
                ChessMove newMove = new ChessMove(ogPosition, possPos, null); //won't ever be promoted on first turn
                moves.add(newMove);
            }
        }
        else if(teamColor == ChessGame.TeamColor.BLACK){
            //black pawn's first move = row - 2 (moving down the board)
            ChessPosition possPos = new ChessPosition(ogPosition.getRow()-2, ogPosition.getColumn());
            if(notBlocked(board, possPos, teamColor) && notFrontBlocked(board, ogPosition, teamColor)){
                ChessMove newMove = new ChessMove(ogPosition, possPos, null); //won't ever be promoted on first turn
                moves.add(newMove);
            }
        }
    }
}

