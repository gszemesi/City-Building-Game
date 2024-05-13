/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package model;

/**
 *
 * @author Akosk
 */
public enum Months {
    JAN(1),FEB(2),MAR(3),APR(4),MAY(5),JUN(6),JUL(7),AUG(8),SEP(9),OCT(10),NOV(11),DEC(12);
    Months(int num){
        this.num=num;
    }
    private static final Months[] vals = values();
    public Months next() {
        return vals[(this.ordinal() + 1) % vals.length];
    }
    public final int num;

    public int getNum() {
        return num;
    }
}
