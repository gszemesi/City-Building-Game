package SQL;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SQLDatabaseTest {
    private SQLDatabase sqlDatabase;

    @BeforeEach
    void setUp() throws SQLException, IOException {
        sqlDatabase = new SQLDatabase("test.db");
        sqlDatabase.createTables();
    }

    @AfterEach
    void tearDown() {
        sqlDatabase.delete();
    }

    @Test
    void createTables() throws SQLException {
        sqlDatabase.dropTables();

        sqlDatabase.createTables();
        Connection connection = sqlDatabase.getConnection();
        assertAll(() -> {
            assertTrue(tableExists(connection, "save"));
            assertTrue(tableExists(connection, "citizen"));
            assertTrue(tableExists(connection, "city"));
        });
    }

    private boolean tableExists(Connection connection, String tableName) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        ResultSet resultSet = meta.getTables(null, null, tableName, new String[]{"TABLE"});

        return resultSet.next();
    }

    @Test
    void dropTables() throws SQLException {
        sqlDatabase.dropTables();

        Connection connection = sqlDatabase.getConnection();
        assertAll(() -> {
            assertFalse(tableExists(connection, "save"));
            assertFalse(tableExists(connection, "city"));
            assertFalse(tableExists(connection, "citizen"));
        });
    }

    @Test
    void close() throws SQLException {
        sqlDatabase.close();
        assertTrue(sqlDatabase.getConnection().isClosed());
    }

    @Test
    void delete() {
        sqlDatabase.delete();
        assertFalse(sqlDatabase.getDatabase().exists());
    }

    @Test
    void insertNewSave() throws SQLException {
        String savename = "test";
        int money = 420;
        int year = 1970, month = 1, week = 1;

        int index = sqlDatabase.insertNewSave(savename, money, year, month, week);
        Connection connection = sqlDatabase.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("""
                SELECT * FROM save WHERE id = ?;
                """);
        preparedStatement.setInt(1, index);
        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            assertAll(() -> {
                assertEquals(savename, resultSet.getString("save_name"));
                assertEquals(money, resultSet.getInt("money"));
                assertEquals(year, resultSet.getInt("year"));
                assertEquals(month, resultSet.getInt("month"));
                assertEquals(week, resultSet.getInt("week"));
            });
        } else {
            fail("No insertions");
        }
    }

    @Test
    void getAllSaves() throws SQLException {
        String[] savenames = {"a", "b", "c"};
        int[] moneys = {1, 2, 3};
        int[][] dates = {{1970, 1, 1}, {1970, 1, 1}, {1970, 1, 1}};
        int[] index = new int[3];

        for (int i = 0; i < 3; i++) {
            index[i] = sqlDatabase.insertNewSave(savenames[i], moneys[i], dates[i][0], dates[i][1], dates[i][2]);
        }

        ArrayList<Save> allSaves = sqlDatabase.getAllSaves();

        for (int i = 0; i < 3; i++) {
            final int finalI = i;
            assertAll(() -> {
                assertEquals(savenames[finalI], allSaves.get(finalI).save_name());
                assertEquals(moneys[finalI], allSaves.get(finalI).money());
                assertEquals(dates[finalI][0], allSaves.get(finalI).year());
                assertEquals(dates[finalI][1], allSaves.get(finalI).month());
                assertEquals(dates[finalI][2], allSaves.get(finalI).week());
                assertEquals(index[finalI], allSaves.get(finalI).id());
            });
        }
    }

    @Test
    void getSave() throws SQLException {
        String[] savenames = {"a", "b", "c"};
        int[] moneys = {1, 2, 3};
        int[][] dates = {{1970, 2, 11}, {1970, 3, 4}, {1970, 1, 1}};
        int[] index = new int[3];

        for (int i = 0; i < 3; i++) {
            index[i] = sqlDatabase.insertNewSave(savenames[i], moneys[i], dates[i][0], dates[i][1], dates[i][2]);
        }

        Optional<Save> save = sqlDatabase.getSave(index[1]);

        save.ifPresentOrElse(save1 -> assertAll(() -> {
            assertEquals(savenames[1], save1.save_name());
            assertEquals(moneys[1], save1.money());
            assertEquals(dates[1][0], save1.year());
            assertEquals(dates[1][1], save1.month());
            assertEquals(dates[1][2], save1.week());
            assertEquals(index[1], save1.id());
        }), Assertions::fail);
    }

    @Test
    void insertCity() throws SQLException {
        int index = sqlDatabase.insertNewSave("test", 32, 1970, 2, 11);

        City city = new City(1, 1, index, 7, 0);

        sqlDatabase.insertCity(city.coordinate_x(), city.coordinate_y(), index, city.flag(), city.level());

        var f = sqlDatabase.getAllCity(index);
        City testCity = f.get(0);

        assertEquals(city, testCity);
    }

    @Test
    void getAllCity() throws SQLException {
        int index = sqlDatabase.insertNewSave("test", 32, 1970, 2, 11);

        ArrayList<City> cities = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            cities.add(new City(i, i, index, 7, 0));
        }

        for (var city : cities) {
            sqlDatabase.insertCity(city.coordinate_x(), city.coordinate_y(), index, city.flag(), city.level());
        }

        var testCity = sqlDatabase.getAllCity(index);
        assertEquals(cities, testCity);
    }

    @Test
    void insertCitizen() throws SQLException {
        int index = sqlDatabase.insertNewSave("test", 32, 1970, 2, 11);
        City city = new City(1, 1, index, 7, 0);
        sqlDatabase.insertCity(city.coordinate_x(), city.coordinate_y(), index, city.flag(), city.level());

        Citizen citizen = new Citizen(1, 20, 50, index, city.coordinate_x(), city.coordinate_y(), city.coordinate_x(), city.coordinate_y());

        sqlDatabase.insertCitizen(citizen.age(), citizen.happiness(), citizen.save_id(),
                citizen.civil_coordinate_x(), citizen.civil_coordinate_y(),
                citizen.work_coordinate_x(), citizen.work_coordinate_y());

        var f = sqlDatabase.getAllCitizen(index);
        Citizen testCitizen = f.get(0);

        assertEquals(citizen, testCitizen);
    }


    @Test
    void getAllCitizen() throws SQLException {
        int index = sqlDatabase.insertNewSave("test", 32, 1970, 2, 11);
        City city = new City(1, 1, index, 7, 0);
        sqlDatabase.insertCity(city.coordinate_x(), city.coordinate_y(), index, city.flag(), city.level());

        ArrayList<Citizen> citizens = new ArrayList<>();

        for (int i = 1; i < 10; i++) {
            citizens.add(new Citizen(i, 20, 50, index, city.coordinate_x(), city.coordinate_y(), city.coordinate_x(), city.coordinate_y()));
        }

        for (var citizen : citizens) {
            sqlDatabase.insertCitizen(citizen.age(), citizen.happiness(), citizen.save_id(),
                    citizen.civil_coordinate_x(), citizen.civil_coordinate_y(),
                    citizen.work_coordinate_x(), citizen.work_coordinate_y());
        }


        var testCitizens = sqlDatabase.getAllCitizen(index);

        assertEquals(citizens, testCitizens);
    }

    @Test
    void deleteSave() throws SQLException {
        int index = sqlDatabase.insertNewSave("test", 32, 1970, 2, 11);
        sqlDatabase.deleteSave(index);

        assertFalse(sqlDatabase.getSave(index).isPresent());
    }

    @Test
    void setAutoCommitTrue() throws SQLException {
        sqlDatabase.setAutoCommitTrue();
        assertTrue(sqlDatabase.getConnection().getAutoCommit());
    }

    @Test
    void setAutoCommitFalse() throws SQLException {
        sqlDatabase.setAutoCommitFalse();
        assertFalse(sqlDatabase.getConnection().getAutoCommit());
    }
}