package dataaccess.memory;

import chess.ChessGame;
import dataaccess.GameDAO;
import model.GameData;
import model.SimplifiedGameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MemoryGameDAO implements GameDAO {
    static final Map<Integer, GameData> games = new HashMap<>();
    //static final Map<String, Integer> gameNameToID = new HashMap<>();

    public MemoryGameDAO() {}

    /* these are test methods*/
    public void addFakeGame() {
        ChessGame fakeChessGame = new ChessGame();
        GameData game1 = new GameData(1234, null, null, "fakeChessGame",
                fakeChessGame);
        games.put(1234, game1);
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
        games.put(1, game1);
        games.put(2, game2);
        games.put(3, game3);

    }
    public boolean isEmpty() {
        return games.isEmpty();
    }

    //--------------------------------------//

    public void clear() {
        games.clear();
    }

    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    public ArrayList<GameData> getGames() {
        ArrayList<GameData> allGames = new ArrayList<>();
        //this exists just to remove the final ChessGame object

        //            SimplifiedGameData curr = new SimplifiedGameData(game.gameID(), game.whiteUsername(), game.blackUsername(),
        //                    game.gameName());
        allGames.addAll(games.values());
        return allGames;
    }

    public void addGame(int gameID, GameData game) {
        games.put(gameID, game);
    }

    public void updateGame(GameData oldGame, GameData newGame) {
        //these should have the exact same ID (because they're copied over)
        games.remove(oldGame.gameID());
        games.put(newGame.gameID(), newGame);
    }

    public void removeGame(int gameID) {
        games.remove(gameID);
    }

    //    public GameData getGame(String gameName){
//        return games.get(gameNameToID.get(gameName));
//    }
}


