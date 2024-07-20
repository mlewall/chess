package dataaccess;

import model.GameData;
import model.SimplifiedGameData;

import java.util.ArrayList;

public interface GameDAO {
    boolean isEmpty();
    void clear();
    void addFakeGame();
    void addManyFakeGames();
    ArrayList<GameData> getGames();

    //GameData getGame(String s);
    GameData getGame(int id);


    void addGame(int gameID, GameData game);
    void updateGame(GameData oldGame, GameData newGame);
}
