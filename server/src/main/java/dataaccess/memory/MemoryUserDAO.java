package dataaccess.memory;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;

import java.util.HashMap;
import java.util.Map;

//crud methods
//extends?
public class MemoryUserDAO implements UserDAO {
    private static final Map<String, UserData> users = new HashMap<>();

    public MemoryUserDAO() {}
    //private static method that returns that instance
    //if isntance is null, create that instance and then return it.

    //a method to retrieve the singular database

    //1) methods to create users

    //2) retrieve user
    public static void insertFakeUser() {
        UserData fake = new UserData("embopgirl", "chomp", "cheese.com");
        users.put("embopgirl", fake);
    }

    public UserData getUserData(String username) throws DataAccessException {
        insertFakeUser(); //for DEBUGGING
        if(users.get(username) != null){
            return users.get(username);
        }
        throw new DataAccessException("Username not found");
        //returns the whole user data, returns null if not found
    }

    //3)update user(?)


    //4) delete user


}
