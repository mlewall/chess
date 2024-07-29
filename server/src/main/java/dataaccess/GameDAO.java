package dataaccess;

import model.GameData;
import model.SimplifiedGameData;

import java.util.ArrayList;

public interface GameDAO {
    boolean isEmpty() throws DataAccessException;
    void clear() throws DataAccessException;

    /*methods for testing*/
    void addFakeGame() throws DataAccessException;;
    void addManyFakeGames() throws DataAccessException;;

    /*methods for the functionality of the DAO*/
    ArrayList<SimplifiedGameData> getGames() throws DataAccessException;;
    GameData getGame(int id) throws DataAccessException;;
    void addGame(int gameID, GameData game) throws DataAccessException;;
    void updateGame(GameData oldGame, GameData newGame) throws DataAccessException;;
}
