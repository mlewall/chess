package dataaccess.database;

import dataaccess.AbstractSqlDAO;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.AuthData;

import java.sql.SQLException;

public class SQLauthDAO extends AbstractSqlDAO implements AuthDAO {
    public SQLauthDAO() throws DataAccessException {
        String[] createUserStatements = {
                """
            CREATE TABLE IF NOT EXISTS authentication (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`)
            )
            """
        };
        configureDatabase(createUserStatements); //adds the table if it hasn't been created yet
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public void addFakeAuth() {

    }

    @Override
    public AuthData getAuthData(String username) {
        return null;
    }

    @Override
    public void addNewAuth(AuthData authData) {

    }

    @Override
    public void remove(String authToken) {

    }
}
