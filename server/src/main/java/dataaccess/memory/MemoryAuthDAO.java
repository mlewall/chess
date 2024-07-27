package dataaccess.memory;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

import javax.xml.crypto.Data;
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

    public boolean isEmpty() throws DataAccessException {
        return AUTHS.isEmpty();
    }

    public void clear() throws DataAccessException {
        AUTHS.clear();
    }

    public AuthData getAuthData(String authToken) throws DataAccessException {
        if(AUTHS.containsKey(authToken)){
            return AUTHS.get(authToken);
        }
        throw new DataAccessException(401, "Error: unauthorized");
    }

    public void addNewAuth(AuthData authData) throws DataAccessException {
        AUTHS.put(authData.authToken(), authData);
    }

    public void remove(String authToken) throws DataAccessException{
        AUTHS.remove(authToken);
    }


}
