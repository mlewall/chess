package dataaccess;

import java.sql.Connection;
import java.sql.SQLException;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public abstract class AbstractSqlDAO {

    public int executeUpdate(String statement, Object... params) throws DataAccessException {
        //var = auto
        try (Connection conn = DatabaseManager.getConnection()) {
            //ps: prepared stmt, protects from injections (??? -> meaning)
            try (var preparedStatement = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) preparedStatement.setString(i + 1, p);
                    else if (param instanceof Integer p) preparedStatement.setInt(i + 1, p);
                        //todo: figure out what this means
                    else if (param == null) preparedStatement.setNull(i + 1, 0);
                }

                //separate executeUpdate method from another class on preparedStatement object
                preparedStatement.executeUpdate();

//                var rs = preparedStatement.getGeneratedKeys();
//                if (rs.next()) {
//                    return rs.getInt(1);
//                }

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
