package dataaccess;

import model.UserData;

import java.sql.SQLException;

public interface UserDAO {
     boolean isEmpty() throws DataAccessException;
     void clear() throws DataAccessException;

     void insertNewUser(UserData userData) throws DataAccessException;
     UserData getUserData(String username) throws DataAccessException;     //public UserData getUserData(String username, String password);

}
