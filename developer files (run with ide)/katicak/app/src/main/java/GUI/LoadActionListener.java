package GUI;

import SQL.Citizen;
import SQL.City;
import SQL.SQLDatabase;
import SQL.Save;
import model.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;

public class LoadActionListener implements ActionListener {
    private final Save save;
    private final BoardGUI boardGUI;
    private final String databasename;

    public LoadActionListener(Save save, BoardGUI boardGUI, String databasename){
        this.save = save;
        this.boardGUI = boardGUI;
        this.databasename = databasename;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        final Mayor mayor = new Mayor(Tax.LOW, true);

        try(SQLDatabase sqlDatabase = new SQLDatabase(databasename)){
            BuildCity(sqlDatabase, mayor);
            addCitizen(sqlDatabase, mayor);
        } catch (SQLException | IOException exception){
            System.err.println(exception.getMessage());
        }

        mayor.setSave(save);
        boardGUI.setMayor(mayor);
        boardGUI.refresh();
    }

    private void BuildCity(SQLDatabase sqlDatabase, Mayor mayor) throws SQLException {
        var cities = sqlDatabase.getAllCity(save.id());
        for(City city: cities){
            int x = city.coordinate_x();
            int y = city.coordinate_y();

            switch (city.flag()){
                case 1 -> mayor.setCivil(x, y);
                case 4 -> mayor.setService(x, y);
                case 7 -> mayor.setIndustrial(x, y);
                case 10 -> mayor.setRoad(x, y);
                case 11 -> mayor.setPolice(x, y);
                case 12 -> mayor.setStadium(x, y);
                case 16 -> {
                    Forest forest = mayor.setForest(x, y);
                    forest.setAge(city.level());
                }
            }
        }
    }

    private void addCitizen(SQLDatabase sqlDatabase, Mayor mayor) throws SQLException {
        var citizens = sqlDatabase.getAllCitizen(save.id());
        var zones = mayor.getOwnsZone();


        for (var citizen : citizens){
            boolean foundCivil = false, foundWork = false;
            Civil home = null;
            Zone work = new Zone(-1, -1, mayor);
            for (var zone : zones) {
                if (civilEqual(zone, citizen)){
                    home = (Civil) zone;
                    foundCivil = true;
                } else if (workEqual(zone, citizen)) {
                    work = zone;
                    if (zone instanceof Service service){
                        service.setNumberOfPeople(service.getNumberOfPeople() + 1);
                    }

                    if (zone instanceof Industrial industrial){
                        industrial.setNumberOfPeople(industrial.getNumberOfPeople() + 1);
                    }
                    foundWork = true;
                }
                if (foundCivil && foundWork){
                    break;
                }
            }
            if (home != null){
                new Person(home, work, mayor, citizen.age());
                home.setNumberOfPeople(home.getNumberOfPeople() + 1);
                home.moveIn();
            } else {
                System.err.println("Error: no home found for citizen");
            }
        }
    }

    private boolean civilEqual(Zone zone, Citizen citizen){
        return zone.getCoordinates().equals(new Point(citizen.civil_coordinate_x(), citizen.civil_coordinate_y()));
    }

    private boolean workEqual(Zone zone, Citizen citizen){
        return zone.getCoordinates().equals(new Point(citizen.work_coordinate_x(), citizen.work_coordinate_y()));
    }
}
