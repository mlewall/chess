package client.websocket;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import chess.ChessMove;
import com.google.gson.Gson;
import org.glassfish.grizzly.http.server.Response;
import server.ResponseException;
import websocket.commands.MoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.*;


//lots like phase 5:
public class WebSocketFacade extends Endpoint {
    Session session;
    NotificationHandler notificationHandler;

    //the notificationHandler will be a repl I believe
    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException{
        try{
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set up message handler: this handles INCOMING messages.
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    switch(serverMessage.getServerMessageType()){
                        case LOAD_GAME -> {LoadGameMessage gameToLoad = new Gson().fromJson(message,
                                LoadGameMessage.class);
                            notificationHandler.updateGame(gameToLoad.getGame());}
                        case NOTIFICATION -> {NotificationMessage notification = new Gson().fromJson(message,
                                NotificationMessage.class);
                            notificationHandler.printMessage(notification.getMessage());}
                        case ERROR -> {ErrorMessage error = new Gson().fromJson(message,
                                ErrorMessage.class);
                            notificationHandler.printMessage(error.getMessage());}
                    }
//                    //account for regular notification vs loadGame vs error (Switch)
//                    NotificationMessage notification = new Gson().fromJson(message, NotificationMessage.class); //null
//                    notificationHandler.printMessage(notification.getMessage());
                }
            });
        }
        catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    /* These methods are for sending stuff to the server*/
    public void connect(String authToken, int gameID) throws ResponseException {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        String jsonCommand = new Gson().toJson(command);
        sendMessage(jsonCommand);
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws ResponseException {
        MoveCommand command = new MoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, move);
        String jsonCommand = new Gson().toJson(command);
        sendMessage(jsonCommand);
    }

    public void leaveGame(String authToken, int gameID) throws ResponseException {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
        String jsonCommand = new Gson().toJson(command);
        sendMessage(jsonCommand);
    }


    public void resignGame(String authToken, int gameID) throws ResponseException {
        UserGameCommand command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
        String jsonCommand = new Gson().toJson(command);
        sendMessage(jsonCommand);
    }

    private void sendMessage(String jsonCommand) throws ResponseException {
        try {
            this.session.getBasicRemote().sendText(jsonCommand);
        }
        catch (IOException ex) {
            throw new ResponseException(500, "Unable to send message");
        }
    }

    //requires this method but it doesn't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}

}
