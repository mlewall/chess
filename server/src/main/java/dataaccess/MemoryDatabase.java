package dataaccess;

import java.util.HashMap;
import java.util.Map;
import model.*;

public class MemoryDatabase {
    static final Map<String, UserData> users = new HashMap<>();
        //String KEY = username (Userdata = username, password, email)
        //username lookup (of UserData) is done in registration and login
    static final Map<String, AuthData> auths = new HashMap<>();
        //KEY: authToken, username (we look things up with the authToken a lot)
    static final Map<String, GameData> games = new HashMap<>();
        //KEY = int GameID, GameData = int GameID, String whiteu, String blacku, String gameName, ChessGame game

    public static void insertFakeUser() {
        UserData fake = new UserData("embopgirl", "chomp", "cheese.com");
        users.put("embopgirl", fake);
    }

    public static UserData getUser(String username) {
        //what happens if you try and look something up in a map and it's not found?
        return users.get(username);
    }
}
