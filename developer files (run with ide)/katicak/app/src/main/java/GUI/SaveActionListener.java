package GUI;

import SQL.SQLDatabase;
import model.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Point;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class SaveActionListener implements ActionListener {
    private Mayor mayor;
    private final BoardGUI boardGUI;
    private final String databasename;

    private final GameGUI jFrame;

    private String saveName;
    public SaveActionListener(BoardGUI boardGUI, String databasename, GameGUI jFrame){
        this.databasename = databasename;
        this.boardGUI = boardGUI;
        this.jFrame = jFrame;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        jFrame.isPaused = true;
        saveName = JOptionPane.showInputDialog(jFrame, "Save name?");
        mayor = boardGUI.getMayor();

        try(SQLDatabase sqlDatabase = new SQLDatabase(databasename)){
            sqlDatabase.setAutoCommitFalse();
            int save_id = insertSave(sqlDatabase);
            insertRoad(sqlDatabase, save_id);
            insertBuildings(sqlDatabase, save_id);
            insertTree(sqlDatabase, save_id);
            insertZone(sqlDatabase, save_id);

            sqlDatabase.commit();
            sqlDatabase.setAutoCommitTrue();
        } catch (SQLException | IOException exception){
            System.err.println(exception.getMessage());
        }


        jFrame.loadSaves();

        jFrame.isPaused = false;
    }

    private int insertSave(SQLDatabase sqlDatabase) throws SQLException {
        return sqlDatabase.insertNewSave(saveName, mayor.getFund(),
                mayor.getYear(), mayor.getMonth().getNum(), mayor.getWeek());
    }

    private void insertRoad(SQLDatabase sqlDatabase, int save_id) throws SQLException{
        ArrayList<Road> roads = mayor.getOwnsRoad();
        for (Road road : roads) {
           Point c = road.getCoordinates();
            sqlDatabase.insertRoad(c.x, c.y, save_id);
        }
    }

    private void insertBuildings(SQLDatabase sqlDatabase, int save_id) throws SQLException {
        ArrayList<Building> buildings = mayor.getOwnsBuilding();
        for (Building building : buildings){
           Point c = building.getCoordinates();

            if (building instanceof Stadium){
                sqlDatabase.insertStadium(c.x, c.y, save_id);
            } else if (building instanceof Police){
                sqlDatabase.insertPolice(c.x, c.y, save_id);
            }
        }
    }

    private void insertTree(SQLDatabase sqlDatabase, int save_id) throws SQLException {
        ArrayList<Forest> forests = mayor.getOwnsForest();
        for (Forest forest : forests){
           Point c = forest.getCoordinates();
            sqlDatabase.insertTree(c.x, c.y, save_id, forest.getAge());
        }
    }

    private void insertZone(SQLDatabase sqlDatabase, int save_id) throws SQLException{
        ArrayList<Zone> zones = mayor.getOwnsZone();
        ArrayList<Person> people = new ArrayList<>();

        for (Zone zone : zones){
           Point c = zone.getCoordinates();

            if (zone instanceof Civil civil){
                sqlDatabase.insertCivil(c.x, c.y, save_id);
                people.addAll(civil.getPeople());
            } else if (zone instanceof Industrial) {
                sqlDatabase.insertIndustrial(c.x, c.y, save_id);
            } else if (zone instanceof Service) {
                sqlDatabase.insertService(c.x, c.y, save_id);
            }
        }

        insertCitizen(sqlDatabase, save_id, people);
    }

    private void insertCitizen(SQLDatabase sqlDatabase, int save_id, ArrayList<Person> people) throws SQLException {
        for (Person person : people){
           Point civil = person.getHome().getCoordinates();
           Point work = person.getWorkplace().getCoordinates();

            sqlDatabase.insertCitizen(person.getAge(), person.getHappiness(), save_id,
                    civil.x, civil.y, work.x, work.y);
        }
    }
}
