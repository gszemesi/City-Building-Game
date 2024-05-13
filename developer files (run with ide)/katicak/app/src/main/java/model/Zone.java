package model;

import java.awt.*;
import java.util.ArrayList;

public class Zone {
    private Point coordinates;
    private ArrayList<Point> canReach;
    private final Mayor m;
    
    public Zone(int x, int y, Mayor m) {
        this.coordinates = new Point(x,y);
        this.m = m;
        this.canReach = new ArrayList<>();
    }
    
    /**
     * út bontásnál figyelni, hogy elérhető-e a koordináta
     * @param coord
     * @return 
     */
    protected boolean roadTo(Point coord){
        return true;
    } // -> Road::demolish(), Road::build()
    
    protected double fullness(){ return 0.0;}
    public boolean canReachZone(Zone destination){ return false; }
    public boolean canReachBuilding(Building destination){ return false; }
    
    // SETTER, GETTER
    public void setCoordinates(Point coordinates) { this.coordinates = coordinates; }
    public void setCanReach(ArrayList<Point> canReach) { this.canReach = canReach; }
    public Point getCoordinates() { return coordinates; }
    public ArrayList<Point> getCanReach() { return canReach; }
    public int getPrice(){ return 1;}
    public Mayor getM() { return m; }
    
}
