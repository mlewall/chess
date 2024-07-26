package dataaccess.database;
import com.google.gson.Gson;


import dataaccess.DataAccessException;
import dataaccess.DatabaseException;
import dataaccess.*;
import model.*;

import java.sql.SQLException;

public class SQLuserDAO implements UserDAO {
    public SQLuserDAO() throws DataAccessException {
        configureDatabase(); //adds the table if it hasn't been created yet
    }

    private final String[] createUserStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
              `username` varchar(256) NOT NULL UNIQUE,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`username`),
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
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
