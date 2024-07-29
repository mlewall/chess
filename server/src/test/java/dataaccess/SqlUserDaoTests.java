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



}
