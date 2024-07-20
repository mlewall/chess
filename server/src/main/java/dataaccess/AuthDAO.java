package dataaccess;

//import exception.DataAccessException;
import model.AuthData;

public interface AuthDAO {
    public boolean isEmpty();
    public void clear();

    public void addFakeAuth();
    public AuthData getAuthData(String username); //UserData contains

    void addNewAuth(AuthData authData);
    void remove(String authToken);
}
