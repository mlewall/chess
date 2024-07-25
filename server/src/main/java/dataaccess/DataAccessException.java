package dataaccess;

/**
 * Indicates there was an error connecting to the database
 */
public class DataAccessException extends Exception{
    final private int statusCode;

    public DataAccessException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    //todo: determine if this will cause problems with the DatabaseManager.
    // The error code will always be 500 when confronting problems with the db (which is maybe good
    // - because they're internal errors
    public DataAccessException(String message){
        super(message);
        statusCode = 500;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
