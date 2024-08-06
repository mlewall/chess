package websocket.messages;

import chess.ChessGame;

/*This class is used by the server to send the
CURRENT GAME STATE to the client. When a client receives this message it
redraws the chessboard.

The game can be any type. It just MUST be called "game".
 */

public class LoadGameMessage extends ServerMessage {
    ChessGame game;

    public LoadGameMessage(ServerMessageType type, ChessGame game) {
        super(type);
        this.game = game;
    }
}
