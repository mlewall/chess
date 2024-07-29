package dataaccess;

import dataaccess.database.SqlAuthDao;
import model.AuthData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class SqlAuthDaoTests {

    static AuthDAO authDAO;


    @BeforeAll
    public static void setUp() {
        try{
            authDAO = new SqlAuthDao();
            authDAO.clear();
        }
        catch (Exception e) {
            System.out.println("Unable to set up Database for testing (check: setUp");
        }
    }

    public void addFakeAuths() throws DataAccessException{
        AuthData authData1 = new AuthData("authToken1", "user1");
        AuthData authData2 = new AuthData("authToken2", "user2");
        authDAO.addNewAuth( authData1);
        authDAO.addNewAuth( authData2);
    }

    @AfterEach
    public void tearDown() {
        try {
            authDAO.clear();
        }
        catch (Exception e) {
            System.out.println("Unable to tear down Database for testing (check: tearDown");
        }
    }

    //check if empty (true)
    @Test
    public void emptyDatabase() throws DataAccessException {
        authDAO.clear();
        assertTrue(authDAO.isEmpty());
    }

    //check if empty (false)
    @Test
    public void nonEmptyDatabase() throws DataAccessException {
        addFakeAuths();
        assertFalse(authDAO.isEmpty());
    }

    //clear (true)
    @Test
    public void clear() throws DataAccessException {
        addFakeAuths();
        assertFalse(authDAO.isEmpty());
        authDAO.clear();
        assertTrue(authDAO.isEmpty());
    }

    //get authData (good authToken)
    @Test
    public void getAuthDataSuccess() throws DataAccessException {
        addFakeAuths();
        AuthData authData = authDAO.getAuthData("authToken1");
        assertTrue(authData != null);
        assertTrue(authData.authToken().equals("authToken1") && authData.username().equals("user1"));
    }

    //get authData (bad authToken)
    @Test
    public void getAuthDataFail() throws DataAccessException {
        addFakeAuths();
        AuthData authData = authDAO.getAuthData("badAuthToken");
        assertNull(authData);
        //the exception is generated in the service class.
    }

    //add new Auth (good data)
    @Test
    public void addNewAuth() throws DataAccessException {
        assertTrue(authDAO.isEmpty());
        addFakeAuths();
        AuthData authData1 = authDAO.getAuthData("authToken1");
        AuthData authData2 = authDAO.getAuthData("authToken2");
        assertNotNull(authData1);
        assertNotNull(authData2);

    }
    //add new Auth (missing something -- something is null)
    @Test
    public void addAuthFail() throws DataAccessException {
        AuthData badAuth = new AuthData(null, "authToken1");
        Exception e = assertThrows(DataAccessException.class, () -> authDAO.addNewAuth(badAuth));
        assertTrue(e.getMessage().contains("Column 'authToken' cannot be null"));
        //System.out.println(e.getMessage());
        //this is actually a bad test
    }

    @Test
    @DisplayName("fail to add duplicate token")
    public void addAuthTokenFail() throws DataAccessException {
        AuthData authData1 = new AuthData("authToken1", "user1");
        authDAO.addNewAuth(authData1);
        AuthData authData2 = new AuthData("authToken1", "user2");
        Exception ex = assertThrows(DataAccessException.class, () -> authDAO.addNewAuth(authData2));
        assertTrue(ex.getMessage().contains("Error: duplicate authData"));
    }

    //ok authToken
    @Test
    public void removeAuthSuccess() throws DataAccessException {
        AuthData authData1 = new AuthData("authToken1", "user1");
        authDAO.addNewAuth(authData1);
        assertFalse(authDAO.isEmpty());
        authDAO.remove("authToken1");
        assertTrue(authDAO.isEmpty());
    }

    //bad authToken
    @Test
    public void removeAuthFail() throws DataAccessException {
        authDAO.addNewAuth(new AuthData("authToken1", "user1"));
        assertFalse(authDAO.isEmpty());
        Exception ex = assertThrows(DataAccessException.class, () -> authDAO.remove("myFavoriteAuthToken"));
        assertFalse(authDAO.isEmpty());
        assertTrue(ex.getMessage().contains("Error: unauthorized"));
    }


}
