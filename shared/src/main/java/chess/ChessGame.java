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
    public boolean isOver;

    //not sure if I want to use cached KingPositions.
    private ChessPosition whiteKingPos;
    private ChessPosition blackKingPos;

    public ChessGame() {
        //DONT CHANGE THIS - required for fullgame and gamestatus to pass
        board.resetBoard(); //all pieces are in their starting locations
        teamTurn = TeamColor.WHITE; //starting turn
        whiteKingPos = null;
        blackKingPos = null;
        isOver = false;
    }

    public ChessGame(ChessGame other) {
        this.board = new ChessBoard(other.board); // Assuming ChessBoard has a copy constructor
        this.teamTurn = other.teamTurn;
        this.whiteKingPos = other.whiteKingPos != null ? new ChessPosition(other.whiteKingPos.getRow(), other.whiteKingPos.getColumn()) : null;
        this.blackKingPos = other.blackKingPos != null ? new ChessPosition(other.blackKingPos.getRow(), other.blackKingPos.getColumn()) : null;
        this.isOver = other.isOver;
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

        teamTurn = startPiece.getTeamColor();
        Collection<ChessMove> unfilteredMoves = startPiece.pieceMoves(board, startPosition);

        for (ChessMove move : unfilteredMoves) {
            //update the board on a copy so it looks like we made the move without calling makeMove
            ChessBoard futureBoard = new ChessBoard(board); //make copy of the original board

            //we need the location of the piece on the future board (future board object)
            ChessPiece movingPiece = futureBoard.getPiece(move.getStartPosition());
            futureBoard.addPiece(move.getEndPosition(), movingPiece);
            futureBoard.addPiece(move.getStartPosition(), null);


            //cache the king positions (as of 8/5 this isn't used really)
            if(teamTurn == TeamColor.BLACK) {
                blackKingPos = getKingPosition(futureBoard, startPiece.getTeamColor());}
            else{
                whiteKingPos = getKingPosition(futureBoard, startPiece.getTeamColor());}

            ChessBoard ogBoard = this.board;
            this.board = futureBoard;

            if(!isInCheck(teamTurn)){
                validMoves.add(move);
            }

            this.board = ogBoard;
        }

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
        ChessPosition startPositionOfMovingPiece = move.getStartPosition();
        ChessPiece movingPiece = board.getPiece(startPositionOfMovingPiece);

        //0) check for if not null spot and not team turn
        if(movingPiece == null){
            throw new InvalidMoveException("Current piece is null for some reason");
        }
        ChessGame.TeamColor currPieceColor = movingPiece.getTeamColor();
        if(currPieceColor != teamTurn){
            throw new InvalidMoveException("Not your team's turn");
        }

        //1) move must be in the collection of validMoves for this piece.
        Collection<ChessMove> validMoves = validMoves(startPositionOfMovingPiece);
        //what if validMoves is empty? then is this just false by default?
        //this depends on equals() working like I want it to! (do I need to override?)
        if(!validMoves.contains(move)){
            throw new InvalidMoveException("Move not in list of valid moves");
        }

        //2) non-pawn/non-promo pawn: do the move on the ACTUAL BOARD;
        ChessPiece capturedPiece = board.getPiece(move.getEndPosition()); //if this piece is not null...(might be null, that is ok)
        if((movingPiece.getPieceType() != ChessPiece.PieceType.PAWN) || move.getPromotionPiece() == null){
            board.addPiece(move.getEndPosition(), movingPiece);
            board.addPiece(move.getStartPosition(), null); //update startPos to null
        }
        //2.5) pawn and promotion piece
        else if(movingPiece.getPieceType() == ChessPiece.PieceType.PAWN && move.getPromotionPiece() != null){
            //make a new piece of the promotion type
            ChessPiece promoPiece = new ChessPiece(teamTurn, move.getPromotionPiece());
            board.addPiece(move.getEndPosition(), promoPiece);
            board.addPiece(move.getStartPosition(), null);
        }

        //3) check for check AFTER all potential moves have been made
        if(isInCheck(teamTurn)){
            //put the pieces back; undo the move
            board.addPiece(move.getStartPosition(), movingPiece);
            board.addPiece(move.getEndPosition(), capturedPiece); //this might be null, might be an actual piece
            throw new InvalidMoveException("Move placed King in check");
        }

        //4) update the king position (if king moved)
        if(movingPiece.getPieceType()== ChessPiece.PieceType.KING){
            if(movingPiece.getTeamColor() == TeamColor.WHITE){
                whiteKingPos = getKingPosition(board, movingPiece.getTeamColor());
            }
            else if(movingPiece.getTeamColor() == TeamColor.BLACK){
                blackKingPos = getKingPosition(board, movingPiece.getTeamColor());
            }
        }

        //5) toggle turn
        toggleTurn();
    }

    private void toggleTurn() {
        if(teamTurn == TeamColor.WHITE){
            teamTurn = TeamColor.BLACK;
        }
        else {
            teamTurn = TeamColor.WHITE;
        }
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

    //the *given method*
    public boolean isInCheck(TeamColor teamColor) {
        //this happens for every possible move the rook can make, for example.
        ChessPosition kingPosition = getKingPosition(board, teamColor);
        return isPositionUnderAttack(kingPosition, teamColor);
    }

    public boolean isPositionUnderAttack(ChessPosition kingPosition, TeamColor teamColor){
        //this is happening AFTER the potential move has been made.
        for(int i = 1; i <= 8; i++){
            for (int j = 1; j <= 8; j++){
                //at this time, the board should temporarily be a *future copy* of the original board (inside the "for")
                ChessPosition scanPos = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(scanPos);
                //if it's an enemy piece
                if(piece != null && piece.getTeamColor() != teamColor){
                    //get all the pieceMoves for that enemyPiece (not considering check...) (I wonder why this works)
                    if(enemyPieceCanAttackKing(kingPosition, piece, scanPos)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean enemyPieceCanAttackKing(ChessPosition kingPosition, ChessPiece piece, ChessPosition scanPos) {
        Collection<ChessMove> possMoves = piece.pieceMoves(board, scanPos);
        for (ChessMove move : possMoves) {
            //if the destination of the enemy's move coincides with the king's current position
            //then the move would put us in check and the move wouldn't be valid.
            if (move.getEndPosition().equals(kingPosition)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the given team has no way to protect their king from danger
     *
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if(isInCheck(teamColor) && hasNoLegalMoves(teamColor)){
            return true;
        }
        //but if every time you call valid moves and check the size of the collection, and the size is 0,
        return false;
    }

    /**
     * Returns true if the given team has no legal moves
     * BUT their king is not in immediate danger.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(hasNoLegalMoves(teamColor) && !isInCheck(teamColor)){
            //and is not in Check (King not in danger)
            return true;
        }
        return false;
    }

    public boolean hasNoLegalMoves(TeamColor teamColor) {
        for(int row = 1; row <= 8; row++){
            for (int col = 1; col <= 8; col++){
                ChessPosition scanPos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(scanPos);
                if(piece != null && piece.getTeamColor() == teamColor){
                    //get all the pieceMoves for that enemyPiece (not considering check...)
                    Collection<ChessMove> validMoves = validMoves(scanPos);
                    if(!validMoves.isEmpty()){
                        return false; //if ANY of them have moves, return false.
                    }
                }
            }
        }
        return true;
    }

    public ChessPosition getKingPosition(ChessBoard board, TeamColor teamColor) {
        //figure out why "pieces cannot eliminate check" and "king cannot move into check" fail
        for(int i = 1; i <= 8; i++){
            for(int j = 1; j <= 8; j++){
                ChessPosition searchPos = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(searchPos);
                if(piece != null){
                    if((piece.getPieceType() == (ChessPiece.PieceType.KING))
                            && (piece.getTeamColor() == (teamColor))){
                        setKingPosition(teamColor, searchPos);
                        return searchPos;
                    }
                }
            }
        }
        return null;
    }

    public void setKingPosition(ChessGame.TeamColor teamColor, ChessPosition searchPos) {
        if(teamColor == TeamColor.BLACK){
            blackKingPos = searchPos;
        }
        else if(teamColor == TeamColor.WHITE){
            whiteKingPos = searchPos;
        }
    }

}

