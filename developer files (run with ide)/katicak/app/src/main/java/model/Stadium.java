package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Objects;

public class Stadium extends Building{
    private final ArrayList<Point> coordinatesArray;
    
    public Stadium(int i, int j, Mayor m) {
        super(i, j, m);
        this.coordinatesArray = new ArrayList<>();
        coordinatesArray.add(new Point(i, j));
        coordinatesArray.add(new Point(i+1, j));
        coordinatesArray.add(new Point(i, j+1));
        coordinatesArray.add(new Point(i+1, j+1));
    }
    
    @Override
    public int satisfaction(Person person){return 1; }
    
    // GETTER, HASHCODE, EQUALS, TOSTRING

    @Override
    public String toString() {
        return "Stadium" + this.getCoordinates();
    }

    public ArrayList<Point> getCoordinatesArray() {
        return coordinatesArray;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Stadium stadium = (Stadium) o;
        return Objects.equals(coordinatesArray, stadium.coordinatesArray);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), coordinatesArray);
    }
}
