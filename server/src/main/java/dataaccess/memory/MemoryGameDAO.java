package dataaccess.memory;

import dataaccess.GameDAO;
import model.GameData;

import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    static final Map<String, GameData> games = new HashMap<>();

    public boolean isEmpty(){
        return games.isEmpty();
    }

    public void clear(){
        games.clear();
    }

    public MemoryGameDAO() {}
}
