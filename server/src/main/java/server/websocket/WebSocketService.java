package server.websocket;

    /* This contains the functions that are called from the WebSocket handler.
    *
    * 1) connect(message): call service and/or send messages to client
    * 2) makeMove(message):
    * 3) leaveGame(message)
    * 4) resignGame(message)
    *
    * These methods are responsible for actually connecting to the DAOs and getting data and stuff.
    * Similar to the functionality of the services we wrote from phase 3.
    *  */

import websocket.commands.UserGameCommand;

public class WebSocketService {
    //todo: determine if this should have any specific fields?
    //note that a command contains: commandType, authToken, gameID, and maybe a Move (if we're in makeMove)
    WebSocketService(){}

    public void connect(UserGameCommand command){}

    public void makeMove(UserGameCommand command){}

    public void leaveGame(UserGameCommand command){}

    public void resignGame(UserGameCommand command){}


}
