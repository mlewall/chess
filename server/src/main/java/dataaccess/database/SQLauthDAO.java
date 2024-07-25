package dataaccess.database;

import dataaccess.AuthDAO;
import model.AuthData;

public class SQLauthDAO implements AuthDAO {
    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void clear() {

    }

    @Override
    public void addFakeAuth() {

    }

    @Override
    public AuthData getAuthData(String username) {
        return null;
    }

    @Override
    public void addNewAuth(AuthData authData) {

    }

    @Override
    public void remove(String authToken) {

    }
}
