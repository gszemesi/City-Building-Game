package model;

public class Police extends Building {
    
    
    public Police(int i, int j, Mayor m) {
        super(i, j, m);
    }

    @Override
    public int satisfaction(Person person){ return 1;}
    
    // GETTER, HASHCODE, EQUALS, TOSTRING
    
    @Override
    public String toString() {
        return "Police" + this.getCoordinates();
    }
    
}
