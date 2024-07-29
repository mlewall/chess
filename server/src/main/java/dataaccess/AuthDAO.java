package dataaccess;

//import exception.DataAccessException;
import model.AuthData;

public interface AuthDAO {
    boolean isEmpty() throws DataAccessException;
    void clear() throws DataAccessException;

    AuthData getAuthData(String username) throws DataAccessException;
    void addNewAuth(AuthData authData) throws DataAccessException;
    void remove(String authToken) throws DataAccessException;
}
