package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn;
    private ChessBoard board = new ChessBoard();
    private ChessPosition WhiteKingPos;
    private ChessPosition BlackKingPos;

    public ChessGame() {
        board.resetBoard(); //all pieces are in their starting locations
        teamTurn = TeamColor.WHITE;//starting turn
        WhiteKingPos = new ChessPosition(1, 5);
        BlackKingPos = new ChessPosition(8, 5);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return this.teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Sets this game's chessboard with a given board (?)
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }



    /**
     * Gets a valid moves for a piece at the given location.
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     *
     * ***how do we know which piece we're talking about? I suppose
     * the start position is provided,
     * and we figure the piece position out from there(?)
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        //YOU cannot make a move that leaves YOUR king in danger
        ChessPiece startPiece = board.getPiece(startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();
        if (startPiece == null){
            return null;} //return null if no piece at start position

        Collection<ChessMove> unfiltered_moves = startPiece.pieceMoves(board, startPosition);
        for (ChessMove move : unfiltered_moves) {
            ChessBoard futureBoard = new ChessBoard(board);
            //update the board so it looks like we made the move without calling makeMove

            ChessPiece movingPiece = futureBoard.getPiece(move.getStartPosition()); //we need the location of the piece on the future board (future board object)
            futureBoard.addPiece(move.getEndPosition(), movingPiece);
            futureBoard.addPiece(move.getStartPosition(), null);
            //copy board here?

            //cache the king positions? just make sure to update them
            if(teamTurn == TeamColor.BLACK) {BlackKingPos = getKingPosition(futureBoard, startPiece.getTeamColor());}
            else{WhiteKingPos = getKingPosition(futureBoard, startPiece.getTeamColor());}

            if(!isInCheck(teamTurn)){
                validMoves.add(move);
            }





        }

            //this function does not call makeMove
            //but we technically haven't made any moves yet.
            //do I need to temporarily copy the board and move that piece?

            //if an enemy piece's MOVE destination matches with the King's destination
            //if the move doesn't leave the king in check, then it's okay, add to validMoves
            //do the move on a copy of the board?
        return validMoves;
    }



    /**
     * Receives a given move and executes it, IF it is a legal move.
     * If the move is illegal:
     * ---+ throw an Invalid Move Exception
     *
     * Illegality:
     * - piece cannot move there
     * - move leaves your king in danger
     * - if it's not your team's turn
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        //a chessmove has a start, an end, and a promotion piece.
        throw new RuntimeException("Not implemented");
        //todo: toggle turn
    }

    /**
     * Determines if the given team is in check.
     * Check means the king is attacked -- the king must move to a position
     * that takes them out of danger. If he has no legal moves to escape,
     * that is checkmate and the game ends immediately.
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */

    public boolean isInCheck(ChessBoard board, ChessPosition kingPosition, TeamColor teamColor){
        //if(the enemy team's pieces have the king's position in one of their possible moves)
        //need(?): position of the current team's king (to be able to compare with
        //why would I need to pass in the chessboard?

        //for square in squares (on board)
        //getPiece at location
        //if(piece != null && piece.getTeamColor != this.teamcolor)
        //piece.getmoves() --> all possible moves
        //for(move:enemyPieceMoves)
        //if(endpos == current King's position)
            //return true;
        return false;
    }

    public boolean isInCheck(TeamColor teamColor) {
        //if the enemy team's pieces have the king in one of their possible moves
        //call pieceMoves on
        //for every spot on the board
        //if there's a piece != null and
        throw new RuntimeException("Not implemented");
    }

    /**
     * Returns true if the given team has no way to protect their king from danger
     *
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Returns true if the given team has no legal moves
     * BUT their king is not in immediate danger.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    public ChessPosition getKingPosition(ChessBoard board, TeamColor teamColor) {
        for(int i = 1; i <= 8; i++){
            for(int j = 1; j <= 8; j++){
                ChessPiece piece = board.getPiece(new ChessPosition(i, j));
                if(piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor){
                    return new ChessPosition(i, j);
                }
            }
        }
        throw new RuntimeException("No King Found? Check @ChessGame getKingPosition");
    }
}
