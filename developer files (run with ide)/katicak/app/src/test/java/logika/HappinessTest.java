package logika;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HappinessTest {
    private Mayor Mayor;

    @BeforeEach
    void setUp() throws Exception {
        Mayor=new Mayor(Tax.LOW,true);
    }

    /**
     * 1. Person::safety():
     *    1. home-nál van rendőrség 60% telítettség alatt
     */
    @Test
    void CivilWithPoliceBelow60(){
        Mayor.buildRoad(0,0);
        Mayor.buildPolice(1,0);
        Civil c=Mayor.buildCivil(0,1);

        Person p = new Person(c, Mayor);

        assertEquals(8,p.safety());
    }

    /**
     * 1. Person::safety():
     *    2. home-nál van rendőrség 60% telítettség felett
     */
    @Test
    void CivilWithPoliceAbove60() {
        Mayor.buildRoad(0,0);
        Mayor.buildPolice(1,0);
        Civil c=Mayor.buildCivil(0,1);

        Person p1 = new Person(c, Mayor);
        Person p2 = new Person(c, Mayor);
        Person p3 = new Person(c, Mayor);
        Person p4 = new Person(c, Mayor);
        Person p5 = new Person(c, Mayor);
        Person p6 = new Person(c, Mayor);
        Person p7 = new Person(c, Mayor);


        assertEquals(13,p1.safety());
    }


    /**
     * 1. Person::safety():
     *    2. home-nál nincs rendőrség 60% telítettség alatt
     */
    @Test
    void CivilWithoutPoliceBelow60() {
        Mayor.buildRoad(0,0);
        Civil c=Mayor.buildCivil(0,1);

        Person p = new Person(c, Mayor);

        assertEquals(-8,p.safety());
    }


    /**
     * 1. Person::safety():
     *    2. home-nál nincs rendőrség 60% telítettség felett
     */
    @Test
    void CivilWithoutPoliceAbove60() {
        Mayor.buildRoad(0,0);
        Civil c=Mayor.buildCivil(0,1);

        Mayor.buildRoad(9,9);
        Industrial i = Mayor.buildIndustrial(9, 8);
        //Mayor.buildPolice(9,8);

        Person p1 = new Person(c,i, Mayor);
        Person p2 = new Person(c,i, Mayor);
        Person p3 = new Person(c,i, Mayor);
        Person p4 = new Person(c,i, Mayor);
        Person p5 = new Person(c,i, Mayor);
        Person p6 = new Person(c,i, Mayor);
        Person p7 = new Person(c,i, Mayor);

        assertEquals(-20,p1.safety());
    }


    /**
     * 1. Person::safety():
     *    3. workplace-nél van rendőrség 60%  telítettség alatt
     */
    @Test
    void WorkPlaceWithPoliceBelow60() {
        Mayor.buildRoad(0,0);
        Civil c=Mayor.buildCivil(0,1);

        Mayor.buildRoad(9,9);
        Industrial i = Mayor.buildIndustrial(9, 8);
        Mayor.buildPolice(9,8);

        Person p = new Person(c, i, Mayor);

        assertEquals(-15,p.safety());
    }


    /**
     * 1. Person::safety():
     *    3. workplace-nél van rendőrség 60%  telítettség felett
     */
    @Test
    void WorkPlaceWithPoliceAbove60() {
        Mayor.buildRoad(0,0);
        Civil c=Mayor.buildCivil(0,1);
        Mayor.buildRoad(9,9);
        Industrial i = Mayor.buildIndustrial(9, 8);
        Mayor.buildPolice(8,9);

        Person p1 = new Person(c,i, Mayor);
        Person p2 = new Person(c,i, Mayor);
        Person p3 = new Person(c,i, Mayor);
        Person p4 = new Person(c,i, Mayor);
        Person p5 = new Person(c,i, Mayor);
        Person p6 = new Person(c,i, Mayor);
        Person p7 = new Person(c,i, Mayor);

        assertEquals(-6,p1.safety());
    }

    /**
     * 2. Person::stadion():
     *    1. nincs stadion
     */
    @Test
    void PersonWithoutStadion() {
        Mayor.buildRoad(0,0);
        Civil c=Mayor.buildCivil(0,1);

        Person p = new Person(c, Mayor);

        assertEquals(0,p.stadion());
    }

    /**
     * 2. Person::stadion():
     *    2. nincs stadion se work, se home közelében
     */
    @Test
    void PersonWithStadionButNotEffected() {
        Mayor.buildRoad(0,0);
        Civil c=Mayor.buildCivil(0,1);
        Mayor.buildRoad(9,9);
        Industrial i = Mayor.buildIndustrial(9, 8);

        Person p = new Person(c, i, Mayor);

        Mayor.buildRoad(0,8);
        Mayor.buildStadium(1,8);

        assertEquals(0,p.stadion());
    }

    /**
     * 2. Person::stadion():
     *    3. stadion csak homenál
     */
    @Test
    void PersonWithStadionToHome() {
        Mayor.buildRoad(0,0);
        Civil c=Mayor.buildCivil(0,1);

        Mayor.buildRoad(9,9);
        Industrial i = Mayor.buildIndustrial(9, 8);

        Person p = new Person(c,i, Mayor);

        Mayor.buildStadium(1,0);

        assertEquals(15,p.stadion());
    }


    /**
     * 2. Person::stadion():
     *    4. stadion csak workplace-nél
     */
    @Test
    void PersonWithStadionToWorkplace() {
        Mayor.buildRoad(0,0);
        Civil c=Mayor.buildCivil(0,1);

        Mayor.buildRoad(9,9);
        Industrial i = Mayor.buildIndustrial(9, 8);

        Person p = new Person(c,i, Mayor);

        Mayor.buildRoad(6,6);
        Mayor.buildStadium(7,6);

        assertEquals(15,p.stadion());
    }

    /**
     * 3. Person::commute():
     *    1. 2> lépésre van a háza a munkahelyétől -> +15
     */
    @Test
    void WorkplaceTwoFieldsAway() {
        Mayor.buildRoad(0,1);
        Civil c=Mayor.buildCivil(0,0);
        Industrial i = Mayor.buildIndustrial(0, 2);

        Person p = new Person(c,i, Mayor);

        assertEquals(15,p.commute());
    }

    /**
     * 3. Person::commute():
     *    2. 3-4 lépésre van -> +10
     */
    @Test
    void WorkplaceThreeFieldsAway() {
        Mayor.buildRoad(0,0);
        Civil c=Mayor.buildCivil(1,0);
        Mayor.buildRoad(0,3);
        Industrial i = Mayor.buildIndustrial(1, 3);

        Person p = new Person(c,i, Mayor);

        assertEquals(10,p.commute());
    }

    /**
     * 3. Person::commute():
     *    3. 5-6 lépésre van -> +5
     */
    @Test
    void WorkplaceFiveFieldsAway() {
        Mayor.buildRoad(0,0);
        Civil c=Mayor.buildCivil(1,0);
        Mayor.buildRoad(0,5);
        Industrial i = Mayor.buildIndustrial(1, 5);

        Person p = new Person(c,i, Mayor);

        assertEquals(5,p.commute());
    }


    /**
     * 3. Person::commute():
     *    3. 5-6 lépésre van -> +5
     */
    @Test
    void WorkplaceSevenFieldsAway() {
        Mayor.buildRoad(0,0);
        Civil c=Mayor.buildCivil(1,0);
        Mayor.buildRoad(0,7);
        Industrial i = Mayor.buildIndustrial(1, 7);

        Person p = new Person(c,i, Mayor);

        assertEquals(-15,p.commute());
    }


    /**
     * 4. Civil::industrialEffect():
     *   1. 2> lépésre van a lakóhelyétől ipari-> -15
     */
    @Test
    void WorkplaceWithinTwoFields() {
        Mayor.buildRoad(0,1);
        Civil c=Mayor.buildCivil(0,0);
        Industrial i = Mayor.buildIndustrial(0, 2);

        Person p = new Person(c,i, Mayor);

        assertEquals(-15,c.industrialEffect());
    }

    /**
     * 4. Civil::industrialEffect():
     *   2. 3-4 lépésre van -> -10
     */
    @Test
    void WorkplaceWithinThreeFields() {
        Mayor.buildRoad(0,0);
        Civil c=Mayor.buildCivil(1,0);
        Mayor.buildRoad(0,3);
        Industrial i = Mayor.buildIndustrial(1, 3);

        Person p = new Person(c,i, Mayor);

        assertEquals(-10,c.industrialEffect());
    }

    /**
     * 4. Civil::industrialEffect():
     *   3. 5 lépésre van -> -5
     */
    @Test
    void WorkplaceWithinFiveFields() {
        Mayor.buildRoad(0,0);
        Civil c=Mayor.buildCivil(1,0);
        Mayor.buildRoad(0,5);
        Industrial i = Mayor.buildIndustrial(1, 5);

        Person p = new Person(c,i, Mayor);

        assertEquals(-5,c.industrialEffect());
    }

    /**
     * 4. Civil::industrialEffect():
     *   4. 5< lépésre van -> +10
     */
    @Test
    void WorkplaceSixFieldsAway() {
        Mayor.buildRoad(0,0);
        Civil c=Mayor.buildCivil(1,0);
        Mayor.buildRoad(0,6);
        Industrial i = Mayor.buildIndustrial(1, 6);

        Person p = new Person(c,i, Mayor);

        assertEquals(10,c.industrialEffect());
    }

    /**
     * 5. Person::tax():
     *   1. ha magas az adó -> -15
     */
    @Test
    void HighTax() {
        Mayor.setTax(Tax.HIGH);
        Mayor.buildRoad(0,0);
        Civil c=Mayor.buildCivil(1,0);
        Mayor.buildRoad(0,6);
        Industrial i = Mayor.buildIndustrial(1, 6);

        Person p = new Person(c,i, Mayor);

        assertEquals(-15,p.tax());
    }

    /**
     * 5. Person::tax():
     *   2. ha alacsony az adó -> 15
     */
    @Test
    void LowTax() {
        Mayor.buildRoad(0,0);
        Civil c=Mayor.buildCivil(1,0);
        Mayor.buildRoad(0,6);
        Industrial i = Mayor.buildIndustrial(1, 6);

        Person p = new Person(c,i, Mayor);

        assertEquals(15,p.tax());
    }

    /**
     * 6. Person::forest():
     *   1. van 3 lépésnyire tőle erdő (hasForest)
     *        a) nincs beépítve (canSee) -> 20
     *        b) be van építve (!canSee) és az industrial -> 15
     *        c) be van építve (esetleg többel is) és egyik sem industrial -> 0
     *        d) be van építve több dologgal is, de az egyik industrial -> 15
     */
    @Test
    void ForestEffect() {
        Mayor.buildRoad(0,0);
        Civil c=Mayor.buildCivil(0,1);

        Mayor.buildRoad(0,9);
        Industrial i = Mayor.buildIndustrial(0, 8);

        Mayor.buildRoad(1,2);
        Mayor.buildRoad(1,3);

        Mayor.plantForest(0,4);

        Person p = new Person(c,i, Mayor);
        for(int j =0;j<11;j++){
            Mayor.forestAging();
        }

        assertAll(() -> {
            //a
            assertEquals(20,p.forest());
            //b
            Mayor.buildIndustrial(0,2);
            assertEquals(15,p.forest());
            //c
            Mayor.demolish(0,2);
            Mayor.buildService(0,2);
            Mayor.buildService(0,3);
            assertEquals(0,p.forest());
            //d
            Mayor.demolish(0,3);
            Mayor.buildIndustrial(0,3);
            assertEquals(15,p.forest());
        });
    }

    /**
     * 6. Person::forest():
     *    2. nincs 3 lépésnyire tőle erdő (hasForest) -> 0
     */
    @Test
    void NoForestEffect() {
        Mayor.buildRoad(0,0);
        Civil c=Mayor.buildCivil(0,1);
        Industrial i = Mayor.buildIndustrial(1, 0);

        Mayor.plantForest(0,5);

        Person p = new Person(c,i, Mayor);

        assertEquals(0,p.forest());
    }


    /**
     * 7. Person::debt():
     */
    @Test
    void Debt() {
        Mayor.buildRoad(0,0);
        Civil c=Mayor.buildCivil(0,1);
        Industrial i = Mayor.buildIndustrial(1, 0);

        Person p = new Person(c,i, Mayor);

        assertAll(() -> {
            //a
            assertEquals(0,p.debt());
            //b
            Mayor.setFund(-1);
            assertEquals(-25,p.debt());
        });
    }
}
