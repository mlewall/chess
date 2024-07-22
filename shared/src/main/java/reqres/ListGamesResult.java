package reqres;

import model.GameData;
import model.SimplifiedGameData;

import java.util.ArrayList;

public record ListGamesResult(ArrayList<SimplifiedGameData> games) implements ServiceResult{}

//public record ListGamesResult(ArrayList<GameData> games) implements ServiceResult{
//}
