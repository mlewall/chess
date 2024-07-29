package dataaccess;

import dataaccess.*;
import dataaccess.database.SqlAuthDao;
import dataaccess.database.SqlGameDao;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import reqres.*;
import model.GameData;

public class SqlGameDaoTests {
    static GameDAO gameDAO;


    @BeforeAll
    public static void setUp() {
        try{
            gameDAO = new SqlGameDao();
            gameDAO.clear();
        }
        catch (Exception e) {
            System.out.println("Unable to set up gameDAO for testing (check: setUp");
        }
    }

    @AfterAll
    public static void tearDown() throws DataAccessException {
        gameDAO.clear();
    }

    //clear

    //is empty True

    //is empty False







}
