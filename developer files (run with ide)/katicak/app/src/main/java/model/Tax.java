package model;

public enum Tax {
    LOW(40), HIGH(81);
    
    Tax(int tax){
        this.tax = tax;
    }
    public final int tax;

    @Override
    public String toString() {
        return "tax: " + tax;
    }
    
    
    
    public int getValue(){
        return tax;
    }
}
