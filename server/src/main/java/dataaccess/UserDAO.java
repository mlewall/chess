package dataaccess;

import model.UserData;

public interface UserDAO {
    public UserData getUserData(String username) throws DataAccessException;
    //public UserData getUserData(String username, String password);
}
