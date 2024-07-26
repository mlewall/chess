package dataaccess.database;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import model.GameData;
import model.SimplifiedGameData;

import java.sql.SQLException;
import java.util.ArrayList;

public class SQLgameDAO implements GameDAO {
    public SQLgameDAO() throws DataAccessException {
        configureDatabase(); //adds the table if it hasn't been created yet
    }

    private final String[] createUserStatements = {
            """
            CREATE TABLE IF NOT EXISTS  games (
              `gameId` INT NOT NULL UNIQUE,
              `whiteUser` varchar(256),
              `blackUser` varchar(256),
              'gameName' varchar(256),
              'game' CLOB NOT NULL,
              PRIMARY KEY (`gameId`)
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
    public void addFakeGame() {

    }

    @Override
    public void addManyFakeGames() {

    }

    @Override
    public ArrayList<SimplifiedGameData> getGames() {
        return null;
    }

    @Override
    public GameData getGame(int id) {
        return null;
    }

    @Override
    public void addGame(int gameID, GameData game) {

    }

    @Override
    public void updateGame(GameData oldGame, GameData newGame) {

    }
}
