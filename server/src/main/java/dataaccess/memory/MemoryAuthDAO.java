package dataaccess.memory;

import dataaccess.AuthDAO;
import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO {
    static final Map<String, AuthData> auths = new HashMap<>();

    public MemoryAuthDAO(){};

    public boolean isEmpty(){
        return auths.isEmpty();
    }

    public void clear(){
        auths.clear();
    }

    public UserData getUser(String username){
        return null;
    }
}
