package model;

import java.util.ArrayList;
import java.util.Objects;

public class Civil extends Zone{
    private final int price = 500;
    private int capacity;
    private final int capacity1 = 10;
    private final int capacity2 = 15;
    private final int capacity3 = 20;
    private int numberOfPeople;
    private ArrayList<Person> people;
    private int x, y;
    private Mayor m;
    private boolean santa;
    private int flagBeforeSanta;

    public Civil(int x, int y, Mayor m) {
        super(x, y, m);
        this.m = m;
        this.x = x;
        this.y = y;
        this.numberOfPeople = 0;
        this.people = new ArrayList();
        this.capacity = capacity1;
        this.santa = false;
    }

    public void santa(){
        this.flagBeforeSanta = m.getFlagsXY(x,y);
        santa = true;
    }

    /**
     * Következő héten
     */
    public void setSantaFalse() {
        m.setFlagsXY(x, y, flagBeforeSanta);
        santa = false;
    }

    public void upgrade(){
        if(capacity == capacity1){
            capacity = capacity2;
            m.setFlagsXY(x, y, 21);
        }
        else{
            capacity = capacity3;
            m.setFlagsXY(x, y, 31);
        }
    }
    
    @Override
    public double fullness(){ return (double)people.size() / capacity; }
    
    /** ! HAPPINESS
     * 1. Végigmenni az emberek listáján
     * 2. Ez alapján átlagolni az elégedettséget
     * @return 
     */
    public int calculateHappiness(){ 
        int happiness = 0;
        for(Person p : people){
            happiness += p.calculateHappiness();
        }
        happiness =(int) Math.floor((double) happiness / people.size());
        
        return happiness; 
    }
    
    /** ! PAYTAX
     *  1. Mayor-től lekérni, hogy HIGH vagy LOW az adó
     *  2. LOW vagy HIGH összege * numberOfPeople értékét hozzáadni a Mayor fund-jához
     */
    public void payTax() {
        int taxpayer = (int) people.stream().filter(Person::isTaxPayer).count();
        m.setFund(m.getFund() + taxpayer * m.getTax().getValue());
    }
    
    /**
     * Építkezzenek lakosok a kijelölt zónán (kijelölés alatt értsd: buildCivil())
     * Telítettségtől függően két flaget fogunk használni (2, 3) a két megjelenítéshez,
     * a megírt kód erre vonatkozik.
     */
    public void moveIn(){
        if(fullness() < 0.5 && capacity == capacity1) m.setFlagsXY(x, y, 2);
        else if(fullness() > 0.5 && capacity == capacity1) m.setFlagsXY(x, y, 3);
        else if(capacity == capacity2) m.setFlagsXY(x, y, 21);
        else if(capacity == capacity3) m.setFlagsXY(x, y, 31);
        
    }
    
    /** ! HAPPINESS
     * 1. Végig menni a Mayor::ownsZone-ján és megkeresni a legközelebbi Industrialt
     * 2. Megnézni ezen Industrial és az aktuális lakózóna közötti mezők távolságát
     * 3. Ha lépés < 2 : - 15
     * 4. Ha lépés < 4 : - 10
     * 5. Ha lépés < 5 : - 5
     * 6. Ha lépés > 5 : + 10
     * @return 
     */
    public int industrialEffect(){
        int minStep = m.getW() + m.getH(); // az átlónál tuti nincs hosszabb. Mivel nem tudjuk
        // biztosra, hogy van-e industrial, ezért nem tudjuk az első industriallal vett távolságot venni
        boolean hasIndustrial = false; // leellenőrizni, hogy van-e industrial,
        // mert nem biztos hogy van. Mivel előfordulhat, hogy tényleg a játéktér
        // két átellenes sarkát nézzük, ezért a step iniciális értéke tkp egy fals érték.
     
        for(Zone zone: m.getOwnsZone()){
            if(zone instanceof Industrial curr){
                hasIndustrial = true;
                int currStep = Math.abs(x - curr.getCoordinates().x) +
                               Math.abs(y - curr.getCoordinates().y);
                if(currStep < minStep) minStep = currStep;
            }
        }
        
        if(hasIndustrial && minStep <= 2) return -15;
        else if(hasIndustrial && minStep < 4) return -10; // (step == 3 || step == 4)
        else if(hasIndustrial && minStep == 5) return -5;
        else return 10;
    }
    
    // GETTER, HASHCODE, EQUALS, TOSTRING
    public int getPrice() { return price; }
    public int getCapacity() { return capacity; }
    public ArrayList<Person> getPeople(){ return people;}
    public int getNumberOfPeople() { return numberOfPeople; }
    public void setNumberOfPeople(int numberOfPeople) { this.numberOfPeople = numberOfPeople; }
    public boolean hasSanta() { return santa; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Civil civil = (Civil) o;
        return numberOfPeople == civil.numberOfPeople && x == civil.x && y == civil.y && Objects.equals(people, civil.people) && Objects.equals(m, civil.m);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numberOfPeople, people, x, y, m);
    }

    public int maintain(){
        int cost = 0;
        for (var person : people) {
            if (!person.isTaxPayer()) {
                cost += ((Tax.LOW.getValue() + Tax.HIGH.getValue()) / 2) * 12;
            }
        }

        return cost;
    }
}
