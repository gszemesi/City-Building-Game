package model;

public class Service extends Zone{
    private final int price = 400;
    private int capacity;
    private final int capacity1 = 5;
    private final int capacity2 = 7;
    private final int capacity3 = 10;
    private int numberOfPeople;
    private final int x,y;
    private final Mayor m;

    public Service(int x, int y, Mayor m) {
        super(x, y, m);
        this.x = x;
        this.y = y;
        this.m = m;
        this.numberOfPeople = 0;
        this.capacity = capacity1;
    }
    
    public void upgrade(){
        if(capacity == capacity1){
            capacity = capacity2;
            m.setFlagsXY(x, y, 51);
        }
        else{
            capacity = capacity3;
            m.setFlagsXY(x, y, 61);
        }
    }
    
    /**
     * Építkezzenek lakosok a kijelölt zónán (kijelölés alatt értsd: buildService())
     * Telítettségtől függően két flaget fogunk használni (5, 6) a két megjelenítéshez,
     * a megírt kód erre vonatkozik.
     */
    public void buildWorkplace(){ 
        if(fullness() < 0.5 && capacity == capacity1) m.setFlagsXY(x, y, 5); 
        else if(fullness() < 0.5 && capacity == capacity2) m.setFlagsXY(x, y, 6);
        else if(capacity == capacity2) m.setFlagsXY(x, y, 51);
        else if(capacity == capacity3) m.setFlagsXY(x, y, 61); 
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
        int hash = 5;
        hash = 23 * hash + this.capacity;
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
        final Service other = (Service) obj;
        return this.capacity == other.capacity;
    }
    
    @Override
    public String toString() { return "Service: " + "capacity=" + capacity; }
}
