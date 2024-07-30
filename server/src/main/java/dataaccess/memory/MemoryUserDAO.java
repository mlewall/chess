package dataaccess.memory;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;

import java.util.HashMap;
import java.util.Map;

//crud methods
//extends?
public class MemoryUserDAO implements UserDAO {
    //the key is the username?
    private static final Map<String, UserData> USERS = new HashMap<>();

    public MemoryUserDAO() {
        //insertFakeUser();
    }

    public boolean isEmpty(){
        return USERS.isEmpty();
    }

    public void clear(){
        USERS.clear();
    }


    public UserData getUserData(String username){
        //insertFakeUser(); //for DEBUGGING
        if(USERS.get(username) != null){
            return USERS.get(username);
        }
        return null;
    }

    public void insertNewUser(UserData user) throws DataAccessException {
        String username = user.username();
        if(USERS.get(username) != null){
            throw new DataAccessException(403, "Error: already taken");
        }
        USERS.put(user.username(), user);
    }

}
