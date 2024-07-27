package dataaccess.database;

import dataaccess.AbstractSqlDAO;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLauthDAO extends AbstractSqlDAO implements AuthDAO {
    public SQLauthDAO() throws DataAccessException {
        String[] createUserStatements = {
                """
            CREATE TABLE IF NOT EXISTS auths (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`)
            )
            """
        };
        configureDatabase(createUserStatements); //adds the table if it hasn't been created yet
    }

    @Override
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
        String statement = "TRUNCATE TABLE auths";
        try {
            executeUpdate(statement);
        }
        catch(Exception e){
            throw new DataAccessException(500, String.format("Error: Unable to clear data: %s", e.getMessage()));
        }
    }

    @Override
    public void addFakeAuth() throws DataAccessException {
        AuthData fake = new AuthData("fakeAuthToken", "fakeUsername");
        String statement = "INSERT INTO authentication (username, password, email) VALUES (?, ?, ?)";
        int id = executeUpdate(statement, fake.authToken(), fake.username());
    }

    @Override
    public AuthData getAuthData(String username) throws DataAccessException {
        try(Connection conn = DatabaseManager.getConnection()){
            String statement = "SELECT * FROM auths WHERE authToken = ?";
            try(var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.setString(1, username);
                try(ResultSet resultSet = preparedStatement.executeQuery()){ //table of results ordered in what you query for
                    if(resultSet.next()){ //start on 0th row; returns if there is more data to read. If there is data,
                        //go to the next row
                        AuthData authData = readAuthData(resultSet);
                        return authData;
                    }
                }
            }
        }
        catch(Exception e){
            throw new DataAccessException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    private AuthData readAuthData(ResultSet resultSet) throws SQLException {
        String authToken = resultSet.getString("authToken");
        String username = resultSet.getString("username");
        return new AuthData(authToken, username);
    }

    @Override
    public void addNewAuth(AuthData authData) throws DataAccessException {
        String statement = "INSERT INTO auths (authToken, username) VALUES (?, ?)";
        //System.out.println(hashedPassword);
        try {
            executeUpdate(statement, authData.authToken(), authData.username());
        }
        catch(DataAccessException e){
            //I don't think we would ever need this
            if(e.getMessage().contains("Duplicate entry")){
                throw new DataAccessException(403, "Error: duplicate authData (should be impossible)");
            }
            else{
                throw new DataAccessException(500, String.format("Error: Unable to insert new authData: %s", e.getMessage()));
            }
        }

    }

    @Override
    public void remove(String authToken) throws DataAccessException {
        String statement = "DELETE FROM auths WHERE authToken = ?";
        try {
            executeUpdate(statement, authToken);
        }
        catch(DataAccessException e){
            throw new DataAccessException(500, String.format("Error: Unable to insert new authData: %s", e.getMessage()));
            }
        }

    }


