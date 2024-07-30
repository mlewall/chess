package dataaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public abstract class AbstractSqlDAO {
    protected abstract String getTableName();

    public boolean isEmpty() throws DataAccessException {
        try(Connection conn = DatabaseManager.getConnection()){
            String query = "SELECT EXISTS (SELECT 1 FROM " + getTableName() + " users LIMIT 1) AS hasRows";
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

    public void clear() throws DataAccessException {
        String statement = "TRUNCATE TABLE " + getTableName();
        try {
            executeUpdate(statement);
        }
        catch(Exception e){
            throw new DataAccessException(500, String.format("Error: Unable to clear data: %s", e.getMessage()));
        }
    }

    public int executeUpdate(String statement, Object... params) throws DataAccessException {
        //var = auto
        try (Connection conn = DatabaseManager.getConnection()) {
            //ps: prepared stmt, protects from injections (??? -> meaning)
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p){
                        ps.setString(i + 1, p);
                    }
                    else if (param instanceof Integer p) {
                        ps.setInt(i + 1, p);
                    }
                        //todo: figure out what this means
                    else if (param == null) {
                        ps.setNull(i + 1, 0);
                    }
                }

                //separate executeUpdate method from another class on ps object
                ps.executeUpdate();

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(500, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    protected void configureDatabase(String[] createStatements) throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

}
