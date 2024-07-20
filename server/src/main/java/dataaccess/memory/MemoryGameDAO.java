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

    public MemoryGameDAO() {
        //todo: remove this, it's just for testing
        //addFakeGame();
    }

    public boolean isEmpty(){
        return games.isEmpty();
    }

    public void clear(){
        games.clear();
    }

    public void addFakeGame(){
        ChessGame fakeChessGame = new ChessGame();
        GameData game1 = new GameData(1234, "", "", "fakeChessGame",
                fakeChessGame);
        games.put(1234, game1);
        //gameNameToID.put("fakeChessGame", 1234);
    }

    public void addManyFakeGames(){
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

    public GameData getGame(int gameID){
        return games.get(gameID);
    }

//    public GameData getGame(String gameName){
//        return games.get(gameNameToID.get(gameName));
//    }

    public void addGame(int gameID, GameData game) {
        games.put(gameID, game);
    }

    public ArrayList<SimplifiedGameData> getAllGames(){
        ArrayList<SimplifiedGameData> allGames = new ArrayList<>();
        //this exists just to remove the final ChessGame object
        for(GameData game: games.values()){
            SimplifiedGameData curr = new SimplifiedGameData(game.gameID(), game.whiteUsername(), game.blackUsername(),
                    game.gameName());
            allGames.add(curr);
        }
        return allGames;
    }
}
