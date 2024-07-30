package dataaccess.database;


import dataaccess.DataAccessException;
import dataaccess.*;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class SqlUserDao extends AbstractSqlDAO implements UserDAO {
    public SqlUserDao() throws DataAccessException {
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

    public void insertNewUser(UserData userData) throws DataAccessException {
        //note that the password is hashed within the service method.
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
        try {
            executeUpdate(statement, userData.username(), hashedPassword, userData.email());
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
    private UserData readUserData(ResultSet resultSet) throws DataAccessException {
        try {
            String username = resultSet.getString("username");
            String password = resultSet.getString("password");
            String email = resultSet.getString("email");
            return new UserData(username, password, email);
        }
        catch(SQLException e){
            throw new DataAccessException(500, "Unable to read UserData from the database into a UserData object");
        }
    }

    @Override
    protected String getTableName() {
        return "users";
    }

}
