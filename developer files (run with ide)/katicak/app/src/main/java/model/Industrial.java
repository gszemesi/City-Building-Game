package model;

import java.util.Objects;

public class Industrial extends Zone{
    private final int price = 300;
    private int capacity;
    private final int capacity1 = 5;
    private final int capacity2 = 7;
    private final int capacity3 = 10;
    private int numberOfPeople;
    private final int x, y;
    private final Mayor m;

    public Industrial(int x, int y, Mayor m) {
        super(x, y, m);
        this.numberOfPeople = 0;
        this.x = x;
        this.y = y;
        this.m = m;
        this.capacity = capacity1;
    }
    
    public void upgrade(){
        if(capacity == capacity1){
            capacity = capacity2;
            m.setFlagsXY(x, y, 81);
        }
        else{
            capacity = capacity3;
            m.setFlagsXY(x, y, 91);
        }
    }
    
    /**
     * Építkezzenek lakosok a kijelölt zónán (kijelölés alatt értsd: buildIndustrial())
     * Telítettségtől függően két flaget fogunk használni (8, 9) a két megjelenítéshez,
     * a megírt kód erre vonatkozik.
     */
    public void buildWorkplace(){ 
        if(fullness() < 0.5 && capacity == capacity1) m.setFlagsXY(x, y, 8); 
        else if(fullness() < 0.5 && capacity == capacity2) m.setFlagsXY(x, y, 9);
        else if(capacity == capacity2) m.setFlagsXY(x, y, 81);
        else if(capacity == capacity3) m.setFlagsXY(x, y, 91);
    }
    
    public void payTax(){ m.setFund(m.getFund() + numberOfPeople * m.getTax().getValue()); }
    
    @Override
    public double fullness(){ return (double)numberOfPeople / capacity; }
    
    // GETTER, HASHCODE, EQUALS, TOSTRING
    public int getPrice() { return price; }
    public int getCapacity() { return capacity; }
    public int getNumberOfPeople() { return numberOfPeople; }
    public void setNumberOfPeople(int numberOfPeople) { this.numberOfPeople = numberOfPeople; }
    
    @Override
    public int hashCode() {
        return Objects.hash(numberOfPeople, x, y, m);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Industrial that = (Industrial) o;
        return numberOfPeople == that.numberOfPeople && x == that.x && y == that.y && Objects.equals(m, that.m);
    }
    
    @Override
    public String toString() { return "Industrial: " + "capacity=" + capacity; }
}
