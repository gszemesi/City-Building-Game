package model;

import java.awt.*;
import java.util.Objects;


public class Road {
    private final int price = 500;
    private final int maintenance = 100;
    private Point coordinates;
    private final Mayor m;
    
    Road(int i, int j, Mayor m){
        this.coordinates = new Point(i, j);
        this.m = m;
    }
    
    /**
     * Bontásnál figyelni, hogy még elérhető-e
     * @param zone
     * @return 
     */
    public boolean isRoadToZone(Zone zone){ return false; }
    public boolean isRoadToBuilding(Building building){ return false; }
    public void build(){}
    /**
     * Mayor::zone tömb segítségével megnézni, hogy elérhető-e minden zóna, amíg elérhető volt eddig
     */
    public void demolish(){}

    // SETTER, GETTER, TOSTRING, HASHCODE, EQUALS
    public int getPrice() { return price; }
    public int getMaintenance() { return maintenance; }
    public Point getCoordinates() { return coordinates; }
    public void setCoordinates(Point coordinates) { this.coordinates = coordinates; }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + Objects.hashCode(this.coordinates);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Road other = (Road) obj;
        return Objects.equals(this.coordinates, other.coordinates);
    }

    @Override
    public String toString() {
        return "Road: " + "price=" + price + ", maintenance=" + maintenance + ", coordinates=" + coordinates;
    }
    
    
}
