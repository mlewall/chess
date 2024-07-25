package dataaccess.database;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;

public class SQLuserDAO implements UserDAO {
    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public void insertNewUser(UserData userData) throws DataAccessException {

    }

    @Override
    public UserData getUserData(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void insertFakeUser() {

    }
}
