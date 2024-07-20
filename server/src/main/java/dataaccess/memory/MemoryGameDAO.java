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
        insertFakeGame();
    }

    public boolean isEmpty(){
        return games.isEmpty();
    }

    public void clear(){
        games.clear();
    }

    public void insertFakeGame(){
        ChessGame fakeChessGame = new ChessGame();
        GameData game1 = new GameData(1234, "", "", "fakeChessGame",
                fakeChessGame);
        games.put(1234, game1);
        //gameNameToID.put("fakeChessGame", 1234);
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
