package dataaccess.database;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.AbstractSqlDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import model.SimplifiedGameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    public boolean isEmpty() throws DataAccessException {
        try(Connection conn = DatabaseManager.getConnection()){
            String query = "SELECT EXISTS (SELECT 1 FROM games LIMIT 1) AS hasRows";
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

    public void addFakeGame() throws DataAccessException{
        ChessGame fakeChessGame = new ChessGame();
        String jsonGame = new Gson().toJson(fakeChessGame);
        GameData game1 = new GameData(1234, null, null, "fakeChessGame",
                fakeChessGame);
        String statement = "INSERT INTO games (gameId, whiteUser, blackUser, gameName, ChessGame) VALUES (?, ?, ?, ?, ?)";
        executeUpdate(statement, game1.gameID(), game1.whiteUsername(), game1.blackUsername(), game1.gameName(), jsonGame);
    }

    public void addManyFakeGames() throws DataAccessException{
        ChessGame fakeChessGame1 = new ChessGame();
        String jsonGame1 = new Gson().toJson(fakeChessGame1);
        ChessGame fakeChessGame2 = new ChessGame();
        String jsonGame2 = new Gson().toJson(fakeChessGame2);
        ChessGame fakeChessGame3 = new ChessGame();
        String jsonGame3 = new Gson().toJson(fakeChessGame3);
        GameData game1 = new GameData(1, "white", "black", "fakeChessGame1",
                fakeChessGame1);
        GameData game2 = new GameData(2, "cloud", "ebony", "fakeChessGame2",
                fakeChessGame2);
        GameData game3 = new GameData(3, "snow", "yeet", "fakeChessGame3",
                fakeChessGame3);
        String statement = "INSERT INTO games (gameId, whiteUser, blackUser, gameName, chessGame) VALUES (?, ?, ?, ?, ?)";
        executeUpdate(statement, game1.gameID(), game1.whiteUsername(), game1.blackUsername(), game1.gameName(), jsonGame1);
        executeUpdate(statement, game2.gameID(), game2.whiteUsername(), game2.blackUsername(), game2.gameName(), jsonGame2);
        executeUpdate(statement, game3.gameID(), game3.whiteUsername(), game3.blackUsername(), game3.gameName(), jsonGame3);
    }

    public ArrayList<SimplifiedGameData> getGames() throws DataAccessException{
        ArrayList<SimplifiedGameData> games = new ArrayList<>();
        try(Connection conn = DatabaseManager.getConnection()){
            String query = "SELECT * FROM games";
            try(PreparedStatement stmt = conn.prepareStatement(query)){
                try(ResultSet resultSet = stmt.executeQuery()){
                    while(resultSet.next()){
                        SimplifiedGameData game = new SimplifiedGameData(
                                resultSet.getInt("gameId"),
                                resultSet.getString("whiteUser"),
                                resultSet.getString("blackUser"),
                                resultSet.getString("gameName")
                        );
                        games.add(game);
                    }
                }
            }
        }
        catch(SQLException e){
            throw new DataAccessException(500, String.format("Unable to access db to list all games: %s", e.getMessage()));
        }
        return games;
    }

    @Override
    public GameData getGame(int gameId) throws DataAccessException{
        try(Connection conn = DatabaseManager.getConnection()){
            String statement = "SELECT * FROM auths WHERE gameId = ?";
            try(PreparedStatement stmt = conn.prepareStatement(statement)){
                stmt.setInt(1, gameId);
                try(ResultSet resultSet = stmt.executeQuery()){
                    if(resultSet.next()){
                        GameData game = new GameData(
                                resultSet.getInt("gameId"),
                                resultSet.getString("whiteUser"),
                                resultSet.getString("blackUser"),
                                resultSet.getString("gameName"),
                                readGame(resultSet.getString("chessGame"))
                        );
                    }
                    return null; //no games were found by that gameId.
                    }
                }
            }
        catch(SQLException e){
            throw new DataAccessException(500, String.format("Unable to get game: %s", e.getMessage()));
        }
    }

    @Override
    public void addGame(int gameID, GameData game) {

    }

    @Override
    public void updateGame(GameData oldGame, GameData newGame) {

    }

    public String getTableName() {
        return "games";
    }

    private ChessGame readGame(String chessGame) throws DataAccessException{
        ChessGame game = new Gson().fromJson(chessGame, ChessGame.class);
        return game;
    }
}
