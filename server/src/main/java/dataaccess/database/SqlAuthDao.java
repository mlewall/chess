package dataaccess.database;

import dataaccess.AbstractSqlDAO;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlAuthDao extends AbstractSqlDAO implements AuthDAO {
    public SqlAuthDao() throws DataAccessException {
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

    public void clear() throws DataAccessException {
        String statement = "TRUNCATE TABLE auths";
        try {
            executeUpdate(statement);
        }
        catch(Exception e){
            throw new DataAccessException(500, String.format("Error: Unable to clear data: %s", e.getMessage()));
        }
    }


    public AuthData getAuthData(String authToken) throws DataAccessException {
        try(Connection conn = DatabaseManager.getConnection()){
            String statement = "SELECT * FROM auths WHERE authToken = ?";
            try(var preparedStatement = conn.prepareStatement(statement)){
                preparedStatement.setString(1, authToken);
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


    public void remove(String authToken) throws DataAccessException {
        String statement = "DELETE FROM auths WHERE authToken = ?";
        //is it in there?
        AuthData authData = getAuthData(authToken);
        if(authData == null){
            throw new DataAccessException(401, "Error: unauthorized");
        }
        try {
            executeUpdate(statement, authToken);
        }
        catch(DataAccessException e){

            throw new DataAccessException(500, String.format("Error: Unable to remove authData: %s", e.getMessage()));
            }
        }

    @Override
    protected String getTableName() {
        return "auths";
    }
}


