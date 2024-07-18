package dataaccess;

//import exception.DataAccessException;
import model.AuthData;
import model.UserData;

import java.util.Collection;

public interface AuthDAO {
    public boolean isEmpty();
    public void clear();

    public UserData getUser(String username); //UserData contains

}
