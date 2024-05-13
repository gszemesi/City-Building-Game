package model;

import java.awt.*;
import java.util.Objects;

public class Forest {
    private final int price = 100;
    private final int maintenance = 50;
    private int distanceFromCivil;
    private final Point coordinates;
    private int age;
    private final Mayor m;
    private final int x,y;

    public Forest(int x, int y, Mayor m) {
        this.coordinates = new Point(x,y);
        this.x = x;
        this.y = y;
        this.age = 0;
        this.m = m;
    }

    /**
     * Minden évben 1 évvel idősebb legyen a fa. Ha elérte a 10. életévét, nem kell
     * utána fenntartási költséget fizetni.
     */
    public void ageUp(){
        age++;
        if(age >= 10){
            m.setFlagsXY(x, y, 17);
        }
    }

    //SETTER, GETTER
    public int getPrice() { return price; }
    public int getMaintenance() { return maintenance; }
    public int getDistanceFromCivil() { return distanceFromCivil; }
    public Point getCoordinates() { return coordinates; }
    public int getAge() { return age; }


    public void setAge(int age) {
        this.age = age;
        if(age >= 10){
            m.setFlagsXY(x, y, 17);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Forest forest = (Forest) o;
        return distanceFromCivil == forest.distanceFromCivil && age == forest.age
                && x == forest.x && y == forest.y
                && Objects.equals(coordinates, forest.coordinates) && Objects.equals(m, forest.m);
    }

    @Override
    public int hashCode() {
        return Objects.hash(distanceFromCivil, coordinates, age, m, x, y);
    }
}

