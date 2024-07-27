package dataaccess.database;
import com.google.gson.Gson;


import dataaccess.DataAccessException;
import dataaccess.DatabaseException;
import dataaccess.*;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SQLuserDAO extends AbstractSqlDAO implements UserDAO {
    public SQLuserDAO() throws DataAccessException {
        String[] createUserStatements = {
                """
            CREATE TABLE IF NOT EXISTS users (
              `username` varchar(256) NOT NULL UNIQUE,
              `password` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL UNIQUE,
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
    public void insertFakeUser() throws DataAccessException {
        UserData fake = new UserData("fakeUsername", "fakePassword", "cheese.com");
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        var id = executeUpdate(statement, fake.username(), fake.password(), fake.email());
    }

    @Override
    public void insertNewUser(UserData userData) throws DataAccessException {
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
        var id = executeUpdate(statement, userData.username(), hashedPassword, userData.email());
    }

    @Override
    public UserData getUserData(String username) throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()){
            var statement = "SELECT * FROM users WHERE username = ?";
            try(var ps = conn.prepareStatement(statement)){
                ps.setString(1, username);
                try(var resultSet = ps.executeQuery()){ //table of results ordered in what you query for
                    if(resultSet.next()){ //start on 0th row; returns if there is more data to read. If there is data,
                        //go to the next row
                        UserData userData = readUserData(resultSet);
                        return userData;
                    }
                }
            }
        }
        catch(Exception e){
            throw new DataAccessException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    //read one row
    private UserData readUserData(ResultSet resultSet) throws SQLException {
        var username = resultSet.getString("username");
        var password = resultSet.getString("password");
        var email = resultSet.getString("email");
        return new UserData(username, password, email);
    }


}
