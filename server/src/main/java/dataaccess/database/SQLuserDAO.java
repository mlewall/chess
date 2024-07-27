package dataaccess.database;
import com.google.gson.Gson;


import dataaccess.DataAccessException;
import dataaccess.DatabaseException;
import dataaccess.*;
import model.*;

import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SQLuserDAO extends AbstractSqlDAO implements UserDAO {
    public SQLuserDAO() throws DataAccessException {
        String[] createUserStatements = {
                """
            CREATE TABLE IF NOT EXISTS users (
              `username` varchar(256) NOT NULL UNIQUE,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              PRIMARY KEY (`username`)
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
    public void clear() throws DataAccessException {
        var statement = "TRUNCATE TABLE users";
        executeUpdate(statement);
    };

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
