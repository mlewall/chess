package dataaccess;

//import exception.DataAccessException;
import model.AuthData;
import model.UserData;

import java.util.Collection;

public interface AuthDAO {
    public boolean isEmpty();
    public void clear();

    public void insertFakeAuth();
    public AuthData getAuthData(String username); //UserData contains

}
