package dataaccess;

public class DatabaseException extends Exception {
    final private int statusCode;

    public DatabaseException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int StatusCode() {
        return statusCode;
    }
}
