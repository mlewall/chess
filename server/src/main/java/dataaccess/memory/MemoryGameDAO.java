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

    /* these are test methods*/
    public void addFakeGame() {
        ChessGame fakeChessGame = new ChessGame();
        GameData game1 = new GameData(1234, null, null, "fakeChessGame",
                fakeChessGame);
        GAMES.put(1234, game1);
        //gameNameToID.put("fakeChessGame", 1234);
    }
    public void addManyFakeGames() {
        //only called in the tests
        ChessGame fakeChessGame1 = new ChessGame();
        ChessGame fakeChessGame2 = new ChessGame();
        ChessGame fakeChessGame3 = new ChessGame();
        GameData game1 = new GameData(1, "white", "black", "fakeChessGame1",
                fakeChessGame1);
        GameData game2 = new GameData(2, "cloud", "ebony", "fakeChessGame2",
                fakeChessGame2);
        GameData game3 = new GameData(3, "snow", "yeet", "fakeChessGame3",
                fakeChessGame3);
        GAMES.put(1, game1);
        GAMES.put(2, game2);
        GAMES.put(3, game3);

    }
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


