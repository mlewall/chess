package server;

public class ResponseException extends Throwable {
    private int statusCode;
    private String message;

    public ResponseException(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
    public int getStatusCode() {
        return statusCode;
    }
    public String getMessage() {
        return message;
    }

}
