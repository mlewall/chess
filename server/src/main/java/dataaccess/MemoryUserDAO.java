package dataaccess;

import model.UserData;

//crud methods

public class MemoryUserDAO implements UserDAO {
    //1) create user

    //2) retrieve user
    @Override
    public UserData getUserData(String username){
        MemoryDatabase.insertFakeUser();
        return MemoryDatabase.getUser(username);
        //returns the whole user data
    }

    //3)update user(?)


    //4) delete user


}
