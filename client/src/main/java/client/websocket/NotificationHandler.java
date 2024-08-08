package client.websocket;
//import webSocketMessages.Notification;

import chess.ChessGame;

public interface NotificationHandler {
    //void notify(Notification notification);
    void updateGame(ChessGame game);
    void printMessage(String message);
}
