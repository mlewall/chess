package reqres;

import java.util.ArrayList;

public record ListGamesResult(ArrayList<model.SimplifiedGameData> allGames) implements ServiceResult{
}
