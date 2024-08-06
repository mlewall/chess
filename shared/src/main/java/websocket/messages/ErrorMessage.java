package websocket.messages;

/*This message is sent to the client when it sends
an invalid command. The message must include the word Error.
 */

public class ErrorMessage extends ServerMessage{
    public String errorMessage;

    public ErrorMessage(ServerMessageType type, String errorMessage) {
        super(type);
        this.errorMessage = errorMessage;

    }
}
