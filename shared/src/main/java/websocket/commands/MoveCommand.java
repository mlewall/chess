package websocket.commands;

import chess.ChessMove;

public class MoveCommand extends UserGameCommand{
    public ChessMove move;

    public MoveCommand(CommandType commandType, String authToken, Integer gameID) {
        super(commandType, authToken, gameID);
    }

    public ChessMove getChessMove(){
        return move;
    }
}
