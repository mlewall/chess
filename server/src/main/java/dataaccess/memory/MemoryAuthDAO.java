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
    public static final Map<String, AuthData> auths = new HashMap<>();

    public MemoryAuthDAO(){};

    public void insertFakeAuth() {
        AuthData fakeAuth = new AuthData("fakeAuthToken", "fakeUsername");
        auths.put("fakeAuthToken", fakeAuth);
    }

    public boolean isEmpty(){
        return auths.isEmpty();
    }

    public void clear(){
        auths.clear();
    }

    public AuthData getAuthData(String authToken){
        return auths.get(authToken);
        //todo: do these need to be the ones to throw the exceptions?
    }

    public void insertNewAuth(AuthData authData){
        auths.put(authData.authToken(), authData);
    }

    public void remove(String authToken){
        auths.remove(authToken);
    }


}
