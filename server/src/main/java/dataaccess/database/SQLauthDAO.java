package dataaccess.database;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.AuthData;

import java.sql.SQLException;

public class SQLauthDAO implements AuthDAO {
    public SQLauthDAO() throws DataAccessException {
        configureDatabase(); //adds the table if it hasn't been created yet
    }

    private final String[] createUserStatements = {
            """
            CREATE TABLE IF NOT EXISTS  authentication (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`)
            )
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createUserStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
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
