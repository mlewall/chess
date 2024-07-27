package dataaccess.database;
import com.google.gson.Gson;


import dataaccess.DataAccessException;
import dataaccess.DatabaseException;
import dataaccess.*;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

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

    public boolean isEmpty() throws DataAccessException {
        try(Connection conn = DatabaseManager.getConnection()){
            String query = "SELECT EXISTS (SELECT 1 FROM users LIMIT 1) AS hasRows";
            try(PreparedStatement stmt = conn.prepareStatement(query)){
                try(ResultSet resultSet = stmt.executeQuery()){
                    if(resultSet.next()){
                        return !resultSet.getBoolean("hasRows");
                    }
                }
            }
        }
        catch(SQLException e){
            throw new DataAccessException(500, String.format("Unable to read data: %s", e.getMessage()));
        }

        return true;
    }

    @Override
    public void clear() throws DataAccessException {
        String statement = "TRUNCATE TABLE users";
        executeUpdate(statement);
    };

    @Override
    public void insertFakeUser() throws DataAccessException {
        UserData fake = new UserData("fakeUsername", "fakePassword", "cheese.com");
        String statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        int id = executeUpdate(statement, fake.username(), fake.password(), fake.email());
    }

    @Override
    public void insertNewUser(UserData userData) throws DataAccessException {
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
        //System.out.println(hashedPassword);
        try {
            int id = executeUpdate(statement, userData.username(), hashedPassword, userData.email());
        }
        catch(DataAccessException e){
            if(e.getMessage().contains("Duplicate entry")){
                throw new DataAccessException(403, "Error: username already taken");
            }
            else{
                throw new DataAccessException(500, String.format("Error: Unable to insert new user: %s", e.getMessage()));
            }
        }

    }

    @Override
    public UserData getUserData(String username) throws DataAccessException {
        try(Connection conn = DatabaseManager.getConnection()){
            String statement = "SELECT * FROM users WHERE username = ?";
            try(var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.setString(1, username);
                try(ResultSet resultSet = preparedStatement.executeQuery()){ //table of results ordered in what you query for
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
