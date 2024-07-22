package dataaccess.memory;

import dataaccess.AuthDAO;
import model.AuthData;

import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO {
    /* Key: authToken (what we mostly use to look things up
        AuthData: an authToken and a username
     */
    public static final Map<String, AuthData> AUTHS = new HashMap<>();

    public MemoryAuthDAO(){};

    public void addFakeAuth() {
        AuthData fakeAuth = new AuthData("fakeAuthToken", "fakeUsername");
        AUTHS.put("fakeAuthToken", fakeAuth);
    }

    public boolean isEmpty(){
        return AUTHS.isEmpty();
    }

    public void clear(){
        AUTHS.clear();
    }

    public AuthData getAuthData(String authToken){
        return AUTHS.get(authToken);
        //todo: do these need to be the ones to throw the exceptions?
    }

    public void addNewAuth(AuthData authData){
        AUTHS.put(authData.authToken(), authData);
    }

    public void remove(String authToken){
        AUTHS.remove(authToken);
    }


}
