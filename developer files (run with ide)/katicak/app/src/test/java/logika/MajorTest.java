package logika;


import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Szemesi Gábor
 */
public class MajorTest {
    private Mayor Mayor;

    @BeforeEach
    void setUp() throws Exception {
        Mayor=new Mayor(Tax.LOW,true);
    }


    /**
     * Mayor(): Low tax
     */
    @Test
    void MayorLowTaxTest() {
        assertEquals(40,Mayor.getTax().getValue());
    }


    /**
     * Mayor(): High tax
     */
    @Test
    void MayorHighaxTest() {
        Mayor=new Mayor(Tax.HIGH,true);
        assertEquals(81,Mayor.getTax().getValue());
    }

    /**
     * buildCivil/Indusrial/Service/Stadium/Police(): út mellet meg épül
     * (elég csak az egyikre letesztelni, mert ugyan azt a fg-t használják - canBuildR() )
     */
    @Test
    void buildNextToRoad() {
        Mayor.buildRoad(1,1);
        Mayor.buildCivil(0,1);

        assertAll(() -> {
            assertEquals("R",Mayor.getCityAt(1,1));
            assertEquals(10,Mayor.getFlagsXY(1,1));
            assertEquals("Z",Mayor.getCityAt(0,1));
            assertEquals(1,Mayor.getFlagsXY(0,1));
        });
    }


    /**
     * buildCivil/Indusrial/Service/Stadium/Police(): út nélkül nem épül meg
     * (elég csak az egyikre letesztelni, mert ugyan azt a fg-t használják - canBuildR() )
     */
    @Test
    void buildNOTNextToRoad() {
        Mayor.buildCivil(0,1);

        assertAll(() -> {
            assertEquals("N/D",Mayor.getCityAt(0,1));
            assertEquals(0,Mayor.getFlagsXY(0,1));
        });
    }


    /**
     * buildCivil/Indusrial/Service/Stadium/Police/Road/Forest(): nem épül meg, ha már van adott koordinátán építve
     * (elég csak az egyik osztályra letesztelni, mert ugyan azt a fg-t használják - canBuildB() )
     */
    @Test
    void buildOnOccupiedLand() {
        Mayor.buildRoad(1,1);
        Mayor.buildCivil(1,1);
        assertAll(() -> {
            assertEquals("R",Mayor.getCityAt(1,1));
            assertEquals(10,Mayor.getFlagsXY(1,1));
        });
    }


    /**
     * buildStaidum(): helyesen épül meg pl (1, 1) koordinátán
     */
    @Test
    void buildStadion() {
        Mayor.buildRoad(0,0);
        Mayor.buildStadium(0,1);
        assertAll(() -> {
            assertEquals("R",Mayor.getCityAt(0,0));
            assertEquals(10,Mayor.getFlagsXY(0,0));

            assertEquals("S",Mayor.getCityAt(0,1));
            assertEquals(12,Mayor.getFlagsXY(0,1));

            assertEquals("S",Mayor.getCityAt(0,2));
            assertEquals(14,Mayor.getFlagsXY(0,2));

            assertEquals("S",Mayor.getCityAt(1,1));
            assertEquals(13,Mayor.getFlagsXY(1,1));

            assertEquals("S",Mayor.getCityAt(1,2));
            assertEquals(15,Mayor.getFlagsXY(1,2));
        });
    }


    /**
     * buildStadium(): nem épül meg a jobb szélén a pályának
     */
    @Test
    void buildStadionRightSideOfTheMap() {
        Mayor.buildRoad(0,8);
        assertEquals("N/D",Mayor.getCityAt(0,9));
        assertEquals(0,Mayor.getFlagsXY(0,9));

        assertEquals("N/D",Mayor.getCityAt(1,9));
        assertEquals(0,Mayor.getFlagsXY(1,9));

        var stadium=Mayor.buildStadium(0,9);
        assertAll(() -> {
            assertEquals("R",Mayor.getCityAt(0,8));
            assertEquals(10,Mayor.getFlagsXY(0,8));

            assertNotEquals("S",Mayor.getCityAt(0,9));
            assertNotEquals(12,Mayor.getFlagsXY(0,9));

            assertNotEquals("S",Mayor.getCityAt(1,9));
            assertNotEquals(14,Mayor.getFlagsXY(1,9));
        });
    }


    /**
     * plantForest(): lehet út nélkül is ültetni
     */
    @Test
    void plantForest() {
        Mayor.plantForest(5,5);
        assertEquals("F",Mayor.getCityAt(5,5));
        assertEquals(16,Mayor.getFlagsXY(5,5));
    }


    /**
     * demolish() - police/zone/forest ág: megépítés után helyesen törölődik
     */
    @Test
    void testDemolish() {
        Mayor.plantForest(3,4);
        assertEquals("F",Mayor.getCityAt(3,4));
        assertEquals(16,Mayor.getFlagsXY(3,4));

        Mayor.demolish(3,4);
        assertEquals("N/D",Mayor.getCityAt(3,4));
        assertEquals(0,Mayor.getFlagsXY(3,4));
    }


    /**
     * demolish() - stadium ág: megépítés után bármelyik 4 koordinátáját kijelölve törlődik helyesen
     */
    @Test
    void testStadionDemolish1() {
        Mayor.buildRoad(0,0);
        Mayor.buildStadium(0,1);
        Mayor.demolish(0,1);
        assertAll(() -> {
            assertEquals("R",Mayor.getCityAt(0,0));
            assertEquals(10,Mayor.getFlagsXY(0,0));

            assertNotEquals("S",Mayor.getCityAt(0,1));
            assertNotEquals(12,Mayor.getFlagsXY(0,1));

            assertNotEquals("S",Mayor.getCityAt(0,2));
            assertNotEquals(14,Mayor.getFlagsXY(0,2));

            assertNotEquals("S",Mayor.getCityAt(1,1));
            assertNotEquals(13,Mayor.getFlagsXY(1,1));

            assertNotEquals("S",Mayor.getCityAt(1,2));
            assertNotEquals(15,Mayor.getFlagsXY(1,2));
        });
    }
    @Test
    void testStadionDemolish2() {
        Mayor.buildRoad(0,0);
        Mayor.buildStadium(0,1);
        Mayor.demolish(0,2);
        assertAll(() -> {
            assertEquals("R",Mayor.getCityAt(0,0));
            assertEquals(10,Mayor.getFlagsXY(0,0));

            assertNotEquals("S",Mayor.getCityAt(0,1));
            assertNotEquals(12,Mayor.getFlagsXY(0,1));

            assertNotEquals("S",Mayor.getCityAt(0,2));
            assertNotEquals(14,Mayor.getFlagsXY(0,2));

            assertNotEquals("S",Mayor.getCityAt(1,1));
            assertNotEquals(13,Mayor.getFlagsXY(1,1));

            assertNotEquals("S",Mayor.getCityAt(1,2));
            assertNotEquals(15,Mayor.getFlagsXY(1,2));
        });
    }
    @Test
    void testStadionDemolish3() {
        Mayor.buildRoad(0,0);
        Mayor.buildStadium(0,1);
        Mayor.demolish(1,1);
        assertAll(() -> {
            assertEquals("R",Mayor.getCityAt(0,0));
            assertEquals(10,Mayor.getFlagsXY(0,0));

            assertNotEquals("S",Mayor.getCityAt(0,1));
            assertNotEquals(12,Mayor.getFlagsXY(0,1));

            assertNotEquals("S",Mayor.getCityAt(0,2));
            assertNotEquals(14,Mayor.getFlagsXY(0,2));

            assertNotEquals("S",Mayor.getCityAt(1,1));
            assertNotEquals(13,Mayor.getFlagsXY(1,1));

            assertNotEquals("S",Mayor.getCityAt(1,2));
            assertNotEquals(15,Mayor.getFlagsXY(1,2));
        });
    }
    @Test
    void testStadionDemolish4() {
        Mayor.buildRoad(0,0);
        Mayor.buildStadium(0,1);
        Mayor.demolish(1,2);
        assertAll(() -> {
            assertEquals("R",Mayor.getCityAt(0,0));
            assertEquals(10,Mayor.getFlagsXY(0,0));

            assertNotEquals("S",Mayor.getCityAt(0,1));
            assertNotEquals(12,Mayor.getFlagsXY(0,1));

            assertNotEquals("S",Mayor.getCityAt(0,2));
            assertNotEquals(14,Mayor.getFlagsXY(0,2));

            assertNotEquals("S",Mayor.getCityAt(1,1));
            assertNotEquals(13,Mayor.getFlagsXY(1,1));

            assertNotEquals("S",Mayor.getCityAt(1,2));
            assertNotEquals(15,Mayor.getFlagsXY(1,2));
        });
    }


    /**
     *  demolish() - road ág: megépítés után 4 különböző sarokban + 4 szélén + mátrix közepén, ha van mellette építve, akkor nem törlődik
     */
    @Test
    void testRoadDemolish1() {
        Mayor.buildRoad(0,0);
        Mayor.demolish(0,0);

        Mayor.buildRoad(0,9);
        Mayor.demolish(0,9);

        Mayor.buildRoad(9,0);
        Mayor.demolish(9,0);

        Mayor.buildRoad(9,9);
        Mayor.demolish(9,9);
        assertAll(() -> {
            assertEquals("N/D",Mayor.getCityAt(0,0));
            assertEquals(0,Mayor.getFlagsXY(0,0));

            assertEquals("N/D",Mayor.getCityAt(0,9));
            assertEquals(0,Mayor.getFlagsXY(0,9));

            assertEquals("N/D",Mayor.getCityAt(9,0));
            assertEquals(0,Mayor.getFlagsXY(9,0));

            assertEquals("N/D",Mayor.getCityAt(9,9));
            assertEquals(0,Mayor.getFlagsXY(9,9));
        });
    }


    /**
     * ratio()
     */
    @Test
    void testRatio(){
        Mayor.buildRoad(0,0);
        Mayor.buildRoad(0,1);
        Mayor.buildRoad(0,2);
        Mayor.buildIndustrial(1,0);
        Mayor.buildService(1,1);
        Mayor.buildService(1,2);
        assertEquals(-1,Mayor.ratio());
        Mayor.buildRoad(0,3);
        Mayor.buildService(1,3);
        assertEquals(-2,Mayor.ratio());
    }

    
    /**
     * maintain(): building and road
     */
    @Test
    void maintainTestBuildingAndRoad() {
        Mayor.buildRoad(1,1);
        Mayor.buildCivil(2,1);
        assertAll(() -> {
            assertEquals(9000,Mayor.getFund());
            Mayor.buildCivil(1,2);
            assertEquals(8500,Mayor.getFund());
        });
    }


    /**
     * maintain(): forest
     */
    @Test
    void maintainForest() {
        Mayor.plantForest(6,7);
        Mayor.plantForest(1,3);
        assertAll(() -> {
            assertEquals(9800,Mayor.getFund());
            Mayor.plantForest(1,3);
            assertEquals(9800,Mayor.getFund());
        });
    }

}
