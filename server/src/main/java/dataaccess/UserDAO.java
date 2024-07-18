package dataaccess;

import model.UserData;

public interface UserDAO {
    public boolean isEmpty();
    public void clear();
    public UserData getUserData(String username) throws DataAccessException;
    //public UserData getUserData(String username, String password);
    public void insertNewUser(UserData userData) throws DataAccessException;

    public void insertFakeUser();
}
