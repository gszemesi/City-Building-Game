package model;

import java.awt.*;
import java.util.Objects;

public class Building{

    private final int price = 2000;
    private final int maintenance = 1000;
    private Point coordinates;
    private final Mayor m;

    public Building(int i, int j, Mayor m) {
        this.coordinates = new Point(i, j);
        this.m = m;
    }
    
    /**
     * Milyen hatást gyakorol a környezetére
     */
    protected int satisfaction(Person person){return 1;};
    
    // SETTER, GETTER, EQUALS
    public int getPrice() { return price; }
    public int getMaintenance() { return maintenance; }
    public Point getCoordinates() { return coordinates; }
    public void setCoordinates(Point coordinates) { this.coordinates = coordinates; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Building building = (Building) o;
        return Objects.equals(coordinates, building.coordinates) && Objects.equals(m, building.m);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coordinates, m);
    }
}
