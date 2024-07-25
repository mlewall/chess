package dataaccess.database;

import dataaccess.GameDAO;
import model.GameData;
import model.SimplifiedGameData;

import java.util.ArrayList;

public class SQLgameDAO implements GameDAO {
    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public void addFakeGame() {

    }

    @Override
    public void addManyFakeGames() {

    }

    @Override
    public ArrayList<SimplifiedGameData> getGames() {
        return null;
    }

    @Override
    public GameData getGame(int id) {
        return null;
    }

    @Override
    public void addGame(int gameID, GameData game) {

    }

    @Override
    public void updateGame(GameData oldGame, GameData newGame) {

    }
}
