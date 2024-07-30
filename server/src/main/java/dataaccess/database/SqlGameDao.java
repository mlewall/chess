package dataaccess.database;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.AbstractSqlDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import model.GameData;
import model.SimplifiedGameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SqlGameDao extends AbstractSqlDAO implements GameDAO {
    public SqlGameDao() throws DataAccessException {
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
            String statement = "SELECT * FROM games WHERE gameId = ?";
            try(PreparedStatement stmt = conn.prepareStatement(statement)){
                stmt.setInt(1, gameId);
                try(ResultSet resultSet = stmt.executeQuery()){
                    if(resultSet.next()){
                        GameData game = new GameData(
                                resultSet.getInt("gameId"),
                                resultSet.getString("whiteUser"),
                                resultSet.getString("blackUser"),
                                resultSet.getString("gameName"),
                                readGame(resultSet.getString("game"))

                        );
                        return game;
                    }
                    return null; //no games were found by that gameId.
                    }
                }
            }
        catch(SQLException e){
            if(e.getMessage().contains("Unknown column")){
                return null;
            }
            throw new DataAccessException(500, String.format("Unable to get game: %s", e.getMessage()));
        }
    }

    @Override
    public void addGame(int gameId, GameData game) throws DataAccessException{
        String statement = "INSERT INTO games (gameId, whiteUser, blackUser, gameName, game) VALUES (?, ?, ?, ?, ?)";
        //System.out.println(hashedPassword);
        String jsonGame = new Gson().toJson(game.game());

        try {
            executeUpdate(statement, gameId, game.whiteUsername(), game.blackUsername(), game.gameName(), jsonGame);
        }
        catch(DataAccessException e){
            //I don't think we would ever need this
            if(e.getMessage().contains("Duplicate entry")){
                throw new DataAccessException(403, "Error: duplicate gameId (should be impossible)");
            }
            else{
                throw new DataAccessException(500, String.format("Error: Unable to insert new game: %s", e.getMessage()));
            }
        }
    }

    @Override
    //the only things that could be different are the whiteUser and blackUser
    public void updateGame(GameData oldGame, GameData newGame) throws DataAccessException{
        String statement = "UPDATE games SET whiteUser = ?, blackUser = ?, game = ? WHERE gameId = ?";
        if(oldGame.gameID() != newGame.gameID()){
            throw new DataAccessException(400, "Error: gameIds of original and new Games do not match");
        }
        String jsonGame = new Gson().toJson(oldGame.game());
        //only update the game if it's different.
        if(!oldGame.game().equals(newGame.game())){
            jsonGame = new Gson().toJson(newGame.game());
        }
        try {
            executeUpdate(statement, newGame.whiteUsername(), newGame.blackUsername(), jsonGame, oldGame.gameID());
        }
        catch(DataAccessException e){
            throw new DataAccessException(500, String.format("Error: Unable to insert new game: %s", e.getMessage()));
        }
    }

    @Override
    protected String getTableName() {
        return "games";
    }

    private ChessGame readGame(String chessGame){
        ChessGame game = new Gson().fromJson(chessGame, ChessGame.class);
        return game;
    }
}
