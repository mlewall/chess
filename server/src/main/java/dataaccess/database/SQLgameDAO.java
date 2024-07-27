package dataaccess.database;

import dataaccess.AbstractSqlDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import model.GameData;
import model.SimplifiedGameData;

import java.sql.SQLException;
import java.util.ArrayList;

public class SQLgameDAO extends AbstractSqlDAO implements GameDAO {
    public SQLgameDAO() throws DataAccessException {
        String[] createGameStatements = {
                """
            CREATE TABLE IF NOT EXISTS games (
              `gameId` INT NOT NULL UNIQUE,
              `whiteUser` varchar(256),
              `blackUser` varchar(256),
              `gameName` varchar(256),
              `game` LONGTEXT NOT NULL,
              PRIMARY KEY (`gameId`)
            )
            """
        };
        configureDatabase(createGameStatements); //adds the table if it hasn't been created yet
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void clear() throws DataAccessException{
        String statement = "TRUNCATE TABLE games";
        try {
            executeUpdate(statement);
        }
        catch(Exception e){
            throw new DataAccessException(500, String.format("Error: Unable to clear data: %s", e.getMessage()));
        }
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
