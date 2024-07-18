package dataaccess.memory;

import dataaccess.AuthDAO;
import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO {
    /* Key: authToken (what we mostly use to look things up
        AuthData: an authToken and a username
     */
    static final Map<String, AuthData> auths = new HashMap<>();

    public void insertFakeAuth() {
        AuthData fake = new AuthData("testAuthToken", "embopgirl");
        auths.put("testAuthToken", fake);
    }

    public MemoryAuthDAO(){};

    public boolean isEmpty(){
        return auths.isEmpty();
    }

    public void clear(){
        auths.clear();
    }

    public AuthData getAuthData(String username){
        return null;
    }
}
