package server.websocket;

    /* This contains the functions that are called from the WebSocket handler.
    *
    * 1) connect(message): call service and/or send messages to client
    * 2) makeMove(message):
    * 3) leaveGame(message)
    * 4) resignGame(message)
    *
    *  */

public class WebSocketService {
    Object message;

    WebSocketService(Object message){
        this.message = message; //it may be variable type?
    }

    public void connect(){}

    public void makeMove(){}

    public void leaveGame(){}

    public void resignGame(){}


}
