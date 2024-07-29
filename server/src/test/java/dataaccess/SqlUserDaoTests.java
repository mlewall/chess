package dataaccess;

import dataaccess.database.SqlUserDao;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reqres.RegisterRequest;
import service.UserService;

import javax.xml.crypto.Data;

import static org.junit.jupiter.api.Assertions.*;


public class SqlUserDaoTests {
    static UserDAO userDAO;

    @BeforeAll
    public static void setUp() {
        try{
            userDAO = new SqlUserDao();
            userDAO.clear();
        }
        catch (Exception e) {
            System.out.println("Unable to set up Database for testing (check: setUp");
        }
    }

    @AfterEach
    public void tearDown() {
        try {
            userDAO.clear();
        }
        catch (Exception e) {
            System.out.println("Unable to tear down Database for testing (check: tearDown");
        }
    }


    @Test
    public void clear(){
        try {
            UserData fakeUser = new UserData("fakeUsername", "fakePassword", "cheese.com");
            userDAO.insertNewUser(fakeUser); //UserData fake = new UserData("fakeUsername", "fakePassword", "cheese.com");
            assertFalse(userDAO.isEmpty());
            userDAO.clear();
            assertTrue(userDAO.isEmpty());
        }
        catch (DataAccessException ex){
            System.out.println(ex.getMessage());
        }
    }

    @Test
    public void insertNewUser() throws Exception{
        UserData newUser = new UserData("myUsername", "myPassword", "myEmail@email.com");
        try {
            userDAO.insertNewUser(newUser);
        }
        catch(DataAccessException ex){
            System.out.println(ex.getMessage());
        }

        assertNotNull(userDAO.getUserData("myUsername"));
    }

    @Test
    public void insertDuplicateUsername() throws DataAccessException {
        UserData newUser = new UserData("myUsername", "myPassword", "myEmail@email.com");

        userDAO.insertNewUser(newUser);

        DataAccessException exception = assertThrows(DataAccessException.class, () -> {
            userDAO.insertNewUser(newUser);
        });

        assertEquals(403, exception.getStatusCode());
        assertEquals("Error: username already taken", exception.getMessage());

    }

    //check if empty (true)
    @Test
    public void emptyDatabase() throws DataAccessException {
        userDAO.clear();
        assertTrue(userDAO.isEmpty());
    }

    //check if empty (false)
    @Test
    public void nonEmptyDatabase() throws DataAccessException {
        userDAO.clear();
        userDAO.insertNewUser(new UserData("fakeUsername", "fakePassword", "fakeEmail@email.com"));
        assertFalse(userDAO.isEmpty());
    }

    //get user data (user exists)
    @Test
    public void retrieveUserDataSuccessful() throws DataAccessException {
        UserData fakeUser = new UserData("fakeUsername", "fakePassword", "fakeEmail@email.com");
        userDAO.insertNewUser(fakeUser);
        UserData retrievedUser = userDAO.getUserData("fakeUsername");
        assertEquals(fakeUser.username(), retrievedUser.username());
        assertEquals(fakeUser.email(), retrievedUser.email());
    }

    //get user data (user doesn't exist)
    @Test
    public void retrieveUserDataUnsuccessful() throws DataAccessException {
        UserData fakeUser = new UserData("fakeUsername", "fakePassword", "fakeEmail@email.com");
        userDAO.insertNewUser(fakeUser);
        assertNull(userDAO.getUserData("incorrectUsername"));
        //this may be a spot for refactoring; considering where DataAccess errors are generated.
        // This one is generated at the service level.
    }
}
