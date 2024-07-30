package dataaccess.memory;

import chess.ChessGame;
import dataaccess.GameDAO;
import model.GameData;
import model.SimplifiedGameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    static final Map<Integer, GameData> GAMES = new HashMap<>();
    //static final Map<String, Integer> gameNameToID = new HashMap<>();

    public MemoryGameDAO() {}


    public boolean isEmpty() {
        return GAMES.isEmpty();
    }

    //--------------------------------------//

    public void clear() {
        GAMES.clear();
    }

    public GameData getGame(int gameID) {
        return GAMES.get(gameID);
    }

    /* make a new list without the actual chess data*/
    public ArrayList<SimplifiedGameData> getGames() {
        ArrayList<SimplifiedGameData> allGames = new ArrayList<>();
        for(GameData game: GAMES.values()) {
            SimplifiedGameData curr = new SimplifiedGameData(game.gameID(), game.whiteUsername(), game.blackUsername(),
                    game.gameName());
            allGames.add(curr);
        }
        return allGames;
    }

    public void addGame(int gameID, GameData game) {
        GAMES.put(gameID, game);
    }

    public void updateGame(GameData oldGame, GameData newGame) {
        GAMES.remove(oldGame.gameID());
        GAMES.put(newGame.gameID(), newGame);
    }

}


