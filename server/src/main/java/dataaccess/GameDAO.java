package dataaccess;

import model.GameData;
import model.SimplifiedGameData;

import java.util.ArrayList;
import java.util.List;

public interface GameDAO {
    boolean isEmpty();
    void clear();
    ArrayList<SimplifiedGameData> getAllGames();

    //GameData getGame(String s);
    GameData getGame(int id);

    void addGame(int gameID, GameData game);
}
