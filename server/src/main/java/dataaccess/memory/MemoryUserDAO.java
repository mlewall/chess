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
    private static final Map<String, UserData> users = new HashMap<>();

    public MemoryUserDAO() {
        //insertFakeUser();
    }

    public boolean isEmpty(){
        return users.isEmpty();
    }

    public void clear(){
        users.clear();
    }

    //1) methods to create users

    //2) retrieve user
    public void insertFakeUser() {
        UserData fake = new UserData("fakeUsername", "fakePassword", "cheese.com");
        users.put("fakeUsername", fake);
    }

    public UserData getUserData(String username) throws DataAccessException {
        //insertFakeUser(); //for DEBUGGING
        if(users.get(username) != null){
            return users.get(username);
        }
        return null;
    }

    public void insertNewUser(UserData user) throws DataAccessException {
        String username = user.username();
        if(users.get(username) != null){
            throw new DataAccessException(403, "Error: already taken");
        }
        users.put(user.username(), user);
    }

}
