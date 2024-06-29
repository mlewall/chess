package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Objects;

public class PawnMovesCalculator implements PieceMovesCalculator {
    public Collection<ChessMove> possPieceMoves(ChessBoard board, ChessPosition og_position){
        int start_row = og_position.getRow();
        int start_column = og_position.getColumn();
        boolean starting_move;
        ChessGame.TeamColor teamColor = board.getPiece(og_position).getTeamColor(); //we should be good getting here, start is inBounds
        Collection<ChessMove> moves = new ArrayList<>();

        //todo: must specify what team color (row 2 + white = start, but not 7)
        if((og_position.getRow() == 2 && teamColor == ChessGame.TeamColor.WHITE)||(og_position.getRow() == 7 && ChessGame.TeamColor.BLACK == teamColor)){
            starting_move = true;
        }
        else{
            starting_move = false;
        }
        //double moves to start, depending on color.
        // These are always straight ahead (in bounds), but also need to be checked for enemies.
        // could be blocked since pieces move. and a move could come later in the game
        if(starting_move && teamColor == ChessGame.TeamColor.WHITE){
            ChessPosition poss_pos = new ChessPosition(og_position.getRow()+2, og_position.getColumn());
            if(!blocked(board, poss_pos, teamColor) && !front_blocked(board, og_position, teamColor)){
                ChessMove new_move = new ChessMove(og_position, poss_pos, null); //won't ever be promoted on first turn
                moves.add(new_move);
            }
        }
        else if(starting_move && teamColor == ChessGame.TeamColor.BLACK){
            //black pawn's first move = row - 2 (moving down the board)
            ChessPosition poss_pos = new ChessPosition(og_position.getRow()-2, og_position.getColumn());
            if(!blocked(board, poss_pos, teamColor) && !front_blocked(board, og_position, teamColor)){
                ChessMove new_move = new ChessMove(og_position, poss_pos, null); //won't ever be promoted on first turn
                moves.add(new_move);
            }
        }
        //----------------WHITE MOVES----------------//
        int[][] white_possibilities = {
                {1, -1}, {1, 0}, {1, 1} //diagonal up, directup, diagonal right
                //right and left are only added if you're taking a piece
                //pawns can only be captured diagonally! So consider
                    //1) is forwards move possible (blocked if straight ahead and other team)
                    //2) is diagonal capture possible (not possible if no enemy piece is there)
        };

        int[][] black_possibilities = {
                {-1, -1}, {-1, 0}, {-1, 1}
        };

        if(teamColor == ChessGame.TeamColor.WHITE) {
            for (int[] white_possibility : white_possibilities) {
                int new_row = start_row + white_possibility[0];
                int new_column = start_column + white_possibility[1];
                ChessPosition poss_position = new ChessPosition(new_row, new_column);

                //move is straight ahead, and it's not blocked (ie no pieces ahead).
                if ((new_column == start_column) && inBounds(new_row, new_column) && !blocked(board, poss_position, teamColor)) {
                    if (promotion(poss_position, teamColor)) {
                        //if it's a promotion, loop through the enum and add those moves.
                        for (ChessPiece.PieceType type : ChessPiece.PieceType.values()) {
                            if ((type.toString() != "PAWN") && (type.toString() != "KING")) {
                                ChessMove new_move = new ChessMove(og_position, poss_position, type);
                                moves.add(new_move);
                            }

                            //this should add all possible promotions to the possible moves
                        }
                    }
                    //not a promotion but still a one-forward step ahead move. Not blocked still, still in bounds, still straight ahead
                    else {
                        ChessMove new_move = new ChessMove(og_position, poss_position, null);
                        moves.add(new_move);
                    }
                } //end if
                //diagonal moves: for capture
                else if ((new_column != start_column) && inBounds(new_row, new_column) && enemyEncounter(board, poss_position, teamColor)) {
                    //but what happens if you capture a piece on a diagonal AND it lets you promote?
                    if (promotion(poss_position, teamColor)) {
                        for (ChessPiece.PieceType type : ChessPiece.PieceType.values()) {
                            if ((type.toString() != "PAWN") && (type.toString() != "KING")) {
                                ChessMove new_move = new ChessMove(og_position, poss_position, type);
                                moves.add(new_move);
                            }

                            //this should add all possible promotions to the possible moves
                        }
                    } else {
                        ChessMove new_move = new ChessMove(og_position, poss_position, null);
                        moves.add(new_move);
                    }

                }
            }
        }
        //------------BLACK POSSIBILITIES-----------------//

        if(teamColor == ChessGame.TeamColor.BLACK) {
            for (int[] black_possibility : black_possibilities) {
                int new_row = start_row + black_possibility[0];
                int new_column = start_column + black_possibility[1];
                ChessPosition poss_position = new ChessPosition(new_row, new_column);

                //move is straight ahead, and it's not blocked (ie no pieces ahead).
                if ((new_column == start_column) && inBounds(new_row, new_column) && !blocked(board, poss_position, teamColor)) {
                    if (promotion(poss_position, teamColor)) {
                        //if it's a promotion, loop through the enum and add those moves.
                        for (ChessPiece.PieceType type : ChessPiece.PieceType.values()) {
                            if((type.toString() != "PAWN") && (type.toString() != "KING")){
                                ChessMove new_move = new ChessMove(og_position, poss_position, type);
                                moves.add(new_move);
                            }
                            //this should add all possible promotions to the possible moves
                        }
                    }
                    //not a promotion but still a one-forward step ahead move. Not blocked still, still in bounds, still straight ahead
                    else{
                        ChessMove new_move = new ChessMove(og_position, poss_position, null);
                        moves.add(new_move);
                    }
                } //end if

                //the problem is that it's finding a blocked forward move and still considering it for this part
                //diagonal moves: for capture
                else if((new_column != start_column) && inBounds(new_row, new_column) && enemyEncounter(board, poss_position, teamColor)){
                    //but what happens if you capture a piece on a diagonal AND it lets you promote?
                    if (promotion(poss_position, teamColor)) {
                        for (ChessPiece.PieceType type : ChessPiece.PieceType.values()) {
                            if((type.toString() != "PAWN") && (type.toString() != "KING")){
                                ChessMove new_move = new ChessMove(og_position, poss_position, type);
                                moves.add(new_move);
                            }

                            //this should add all possible promotions to the possible moves
                        }
                    }
                    else{
                        ChessMove new_move = new ChessMove(og_position, poss_position, null);
                        moves.add(new_move);
                    }
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

    private boolean enemyEncounter(ChessBoard board, ChessPosition end, ChessGame.TeamColor teamcolor){
        ChessPiece pieceAtDest = board.getPiece(end);
        if(pieceAtDest != null && teamcolor != pieceAtDest.getTeamColor()){
            return true;
        }
        return false;
    }

    private boolean promotion(ChessPosition endPos, ChessGame.TeamColor teamColor){
        //the correct color made it all the way across
        if((endPos.getRow() == 8) && (teamColor == ChessGame.TeamColor.WHITE)){
            return true;
        }
        else if((endPos.getRow() == 1) && (teamColor == ChessGame.TeamColor.BLACK)){
            return true;
        }
        return false;
    }

    private boolean front_blocked(ChessBoard board, ChessPosition og_position, ChessGame.TeamColor teamcolor){
        if(teamcolor == ChessGame.TeamColor.WHITE){
            ChessPosition front = new ChessPosition(og_position.getRow()+1, og_position.getColumn());
            ChessPiece pieceAtFront = board.getPiece(front);
            if(pieceAtFront != null){
                return true;
            }
        }
        else if(teamcolor == ChessGame.TeamColor.BLACK){
            ChessPosition front = new ChessPosition(og_position.getRow()-1, og_position.getColumn());
            ChessPiece pieceAtFront = board.getPiece(front);
            if(pieceAtFront != null){
                return true;
            }
        }
        return false;
        }

    private boolean blocked(ChessBoard board, ChessPosition poss_position, ChessGame.TeamColor teamcolor){
        //if there is a piece of the same color at that possible destination, return false
        //todo: add an inbounds check here?
        ChessPiece pieceAtDest = board.getPiece(poss_position);
        if(pieceAtDest == null){
            return false;
        }
        //todo: delete the code below here: I think it doesn't matter bc pawn can't move forward at all if blocked
        else if(pieceAtDest.getTeamColor() == teamcolor){
            return true;
            //blocked by our own teammate! NOOOO!
        }
        else if(pieceAtDest.getTeamColor() != teamcolor){
            //TODO: remember to change this for pawns!
            return true;
        };
        return false;
    }
}

