package model;

import SQL.Save;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

public class Mayor {

    private final int startingFund = 10000;
    private int fund;
    private final ArrayList<Zone> ownsZone;
    private final ArrayList<Building> ownsBuilding;
    private final ArrayList<Road> ownsRoad;
    private final ArrayList<Forest> ownsForest;
    private Tax tax;
    private final int w, h;
    private final int[][] flags;   // konkrét kirajzolandó elemek 0-17
    private final String[][] city; // építésnél demolishnál általánosított (nincs külön szedve beépített és kijelölt zóna)
    private int year;
    private Months month;
    private int week;
    private int weekStepNum;
    private int SpeedOfTimeNum; // SpeedOfTimenum is 6 if the speed is normal, 3 if it's fast, and 2 if it's very fast (used only in timerstep()) 
    private int moveInBuffer; //Number of people wants to move into the city
    private int criticalHappinessCounter = 0; // Counter of months when a player is reaching GameOver()

    public void upgrade(int x, int y){
        for(Zone z : ownsZone){
            if(z instanceof Civil c && z.getCoordinates().equals(new Point(x,y))){
                c.upgrade();
                fund -= 500;
            }
            else if(z instanceof Industrial i && z.getCoordinates().equals(new Point(x,y))){
                i.upgrade();
                fund -= 500;
            }
            else if(z instanceof Service s && z.getCoordinates().equals(new Point(x,y))){
                s.upgrade();
                fund -= 500;
            }
        }
    }

    public Mayor(Tax tax) {
        this(tax, false);
    }

    //Teszteléshez, hogy a random generálás ne zavarjon
    public Mayor(Tax tax, boolean noForest) {
        this.fund = startingFund;
        this.tax = tax;
        this.ownsZone = new ArrayList<>();
        this.ownsBuilding = new ArrayList<>();
        this.ownsRoad = new ArrayList<>();
        this.ownsForest = new ArrayList<>();
        this.w = 10;
        this.h = 10;
        this.year = 1970;
        this.month = Months.JAN;
        this.week = 1;
        this.SpeedOfTimeNum = 6;
        moveInBuffer = 5;

        this.flags = new int[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                flags[i][j] = 0;
            }
        }

        this.city = new String[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                city[i][j] = "N/D";
            }
        }
        if (!noForest) {
            randomForests();
        }
    }

    /**
     * Konstruktortól függően játék elején generál erdőket.
     */
    private void randomForests() {
        Random rand = new Random();
        int numOfForests = rand.nextInt(4) + 1;

        for (int i = 0; i < numOfForests; i++) {
            int x = rand.nextInt(h);
            int y = rand.nextInt(w);
            if (city[x][y] == "N/D") {
                Forest f = new Forest(x, y, this);
                ownsForest.add(f);
                city[x][y] = "F";
                f.setAge(20);
            }
        }
    }

    private void generateSanta(){
        Random rand = new Random();
        int randInd = rand.nextInt(ownsZone.size());

        ArrayList<Integer> civilInds = new ArrayList();
        for (int i = 0; i < ownsZone.size(); i++) {
            if (ownsZone.get(i) instanceof Civil) {
                civilInds.add(i);
            }
        }
        Civil homeWithSanta = (Civil) ownsZone.get(randInd);
        homeWithSanta.santa();
    }

    /**
     * Levonja az épületek, facsemeték és utak fenntartási költségét a
     * polgármester tőkéjéből.
     */
    public void maintain() {
        Building b = new Building(-1, -1, this);
        Forest f = new Forest(-1, -1, this);
        Road r = new Road(-1, -1, this);

        int numSaplings = 0;
        for (Forest forest : ownsForest) {
            if (forest.getAge() < 10) {
                numSaplings++;
            }
        }

        int notTaxPayerFund = 0;
        for (var zone : ownsZone) {
            if (zone instanceof Civil civil){
                notTaxPayerFund += civil.maintain();
            }
        }

        fund -= b.getMaintenance() * ownsBuilding.size()
                + f.getMaintenance() * numSaplings
                + r.getMaintenance() * ownsRoad.size()
                + notTaxPayerFund;
    }

    /**
     * Felépít a city és flags mátrixba egy stadiont. Kijelölt
     * koordinátától jobbra és balra építkezik.
     *
     * @param x
     * @param y
     * @return
     */
    public Stadium buildStadium(int x, int y) {
        if (canBuildB(x, y) && canBuildR(x, y)) {
            if (x + 1 < h && city[x + 1][y].compareTo("N/D") == 0) {
                if (y + 1 < w && city[x][y + 1].compareTo("N/D") == 0
                        && city[x + 1][y + 1].compareTo("N/D") == 0) {
                    // MÉG ELLEN�?RIZNI, HOGY SZABADOK-E A HELYEK
                    Stadium s = setStadium(x, y);
                    fund -= s.getPrice();
                    return s;
                } else {
                    return new Stadium(-1, -1, this);
                }
            } else {
                return new Stadium(-1, -1, this);
            }
        } else {
            return new Stadium(-1, -1, this);
        }
    }

    public Stadium setStadium(int x, int y) {
        Stadium s = new Stadium(x, y, this); // !!! összes koordinátáját tárolni
        ownsBuilding.add(s);
        ArrayList<Point> coords = s.getCoordinatesArray();
        for (int i = 0; i < coords.size(); i++) {
            int xi = coords.get(i).x;
            int yi = coords.get(i).y;
            flags[xi][yi] = 12 + i;
            city[xi][yi] = "S";
        }
        return s;
    }

    /**
     * buildCivil/Industrial/Service/Police(): felépít út mellett megfelelő
     * zónát/épületet, amennyiben nincs már építve kijeölt
     * koordinátára.
     *
     * @param x
     * @param y
     * @return sikeres építés esetén az objuektum, egyébként egy fals
     * koordinátájú objektum
     */
    public Police buildPolice(int x, int y) {
        if (canBuildB(x, y) && canBuildR(x, y)) {
            Police p = setPolice(x, y);
            fund -= p.getPrice();
            return p;
        } else {
            return new Police(-1, -1, this);
        }
    }

    public Police setPolice(int x, int y) {
        Police p = new Police(x, y, this);
        ownsBuilding.add(p);
        city[x][y] = "P";
        flags[x][y] = 11;
        return p;
    }

    public Civil buildCivil(int x, int y) {
        if (canBuildB(x, y) && canBuildR(x, y)) {
            Civil c = setCivil(x, y);
            fund -= c.getPrice();
            return c;
        }
        return new Civil(-1, -1, this);
    }

    public Civil setCivil(int x, int y) {
        Civil c = new Civil(x, y, this);
        ownsZone.add(c);
        city[x][y] = "Z";
        flags[x][y] = 1;
        return c;
    }

    public Industrial buildIndustrial(int x, int y) {
        if (canBuildB(x, y) && canBuildR(x, y)) {
            Industrial i = setIndustrial(x, y);
            fund -= i.getPrice();
            return i;
        }
        return new Industrial(-1, -1, this);
    }

    public Industrial setIndustrial(int x, int y) {
        Industrial i = new Industrial(x, y, this);
        ownsZone.add(i);
        city[x][y] = "Z";
        flags[x][y] = 7;
        return i;
    }

    public Service buildService(int x, int y) {
        if (canBuildB(x, y) && canBuildR(x, y)) {
            Service s = setService(x, y);
            fund -= s.getPrice();
            return s;
        }
        return new Service(-1, -1, this);
    }

    public Service setService(int x, int y) {
        Service s = new Service(x, y, this);
        ownsZone.add(s);
        city[x][y] = "Z";
        flags[x][y] = 4;
        return s;
    }

    public Road buildRoad(int x, int y) {
        if (canBuildB(x, y)) {
            Road r = setRoad(x, y);
            fund -= r.getPrice();
            return r;
        } else {
            return new Road(-1, -1, this);
        }
    }

    public Road setRoad(int x, int y) {
        Road r = new Road(x, y, this);
        ownsRoad.add(r);
        city[x][y] = "R";
        flags[x][y] = 10;
        return r;
    }

    public Forest plantForest(int x, int y) {
        if (canBuildB(x, y)) {
            Forest f = setForest(x, y);
            fund -= f.getPrice();
            return f;
        } else {
            return new Forest(-1, -1, this);
        }
    }

    public Forest setForest(int x, int y) {
        Forest f = new Forest(x, y, this);
        ownsForest.add(f);
        city[x][y] = "F";
        flags[x][y] = 16;
        return f;
    }

    /**
     * Objektumok törlése a pályáról. Minden objektumot külön
     * segédfüggvénnyel töröl.
     *
     * @param x
     * @param y
     */
    public void demolish(int x, int y) {
        if (city[x][y].compareTo("P") == 0) {
            demolishPolice(x, y);
        } else if (city[x][y].compareTo("S") == 0) {
            demolishStadium(x, y);
        } else if (city[x][y].compareTo("Z") == 0) {
            demolishZone(x, y);
        } else if (city[x][y].compareTo("R") == 0 && canDemolish(x, y)) {
            demolishRoad(x, y);
        } else if (city[x][y].compareTo("F") == 0) {
            demolishForest(x, y);
        }
    }

    private void demolishPolice(int x, int y){
        boolean found = false;
            int ind = 0;
            for (int i = 0; i < ownsBuilding.size() && !found; i++) {
                if (ownsBuilding.get(i).getCoordinates().equals(new Point(x, y))) {
                    found = true;
                    ind = i;
                }
            }
            if (found) {
                fund += ownsBuilding.get(ind).getPrice();
                ownsBuilding.remove(ind);
                city[x][y] = "N/D";
                flags[x][y] = 0;
            }
    }

    private void demolishStadium(int x, int y){
        Stadium s = findStadium(x, y);
            boolean found = false;
            int ind = 0;
            for (int i = 0; i < ownsBuilding.size() && !found; i++) {
                if (ownsBuilding.get(i).equals(s)) {
                    found = true;
                    ind = i;
                }
            }
            if (found) {
                fund += ownsBuilding.get(ind).getPrice();
                ownsBuilding.remove(ind);
                ArrayList<Point> coords = s.getCoordinatesArray();
                for (Point coord : coords) {
                    int xi = coord.x;
                    int yi = coord.y;
                    flags[xi][yi] = 0;
                    city[xi][yi] = "N/D";
                }
            }
    }

    private void demolishZone(int x, int y){
        boolean found = false;
            int ind = 0;
            for (int i = 0; i < ownsZone.size() && !found; i++) {
                if (ownsZone.get(i).getCoordinates().equals(new Point(x, y))) {
                    found = true;
                    ind = i;
                }
            }
            if (found) {
                fund += ownsZone.get(ind).getPrice();
                ownsZone.remove(ind);
                city[x][y] = "N/D";
                flags[x][y] = 0;
            }
    }

    private void demolishRoad(int x, int y){
    boolean found = false;
            int ind = 0;
            for (int i = 0; i < ownsRoad.size() && !found; i++) {
                if (ownsRoad.get(i).getCoordinates().equals(new Point(x, y))) {
                    found = true;
                    ind = i;
                }
            }
            if (found) {
                fund += ownsRoad.get(ind).getPrice();
                ownsRoad.remove(ind);
                city[x][y] = "N/D";
                flags[x][y] = 0;
            }
    }

    private void demolishForest(int x, int y){
        boolean found = false;
            int ind = 0;
            for (int i = 0; i < ownsForest.size() && !found; i++) {
                if (ownsForest.get(i).getCoordinates().equals(new Point(x, y))) {
                    found = true;
                    ind = i;
                }
            }
            if (found) {
                fund += ownsForest.get(ind).getPrice();
                ownsForest.remove(ind);
                city[x][y] = "N/D";
                flags[x][y] = 0;
            }
    }

    /**
     * A demolish() segédfüggvénye. Játéktéren kijelölt koordináta
     * alapján megkeresi az ownsBuilding listából a törlendő stadiont.
     *
     * @param x
     * @param y
     * @return
     */
    public Stadium findStadium(int x, int y) {
        Stadium s = new Stadium(-1, -1, this);
        for (Building b : ownsBuilding) {
            if (b instanceof Stadium stadium) {
                ArrayList<Point> coords = stadium.getCoordinatesArray();
                for (Point coord : coords) {
                    if (coord.x == x && coord.y == y) {
                        s = stadium;
                        break;
                    }
                }
            }
        }
        return s;
    }

    /**
     * A build() függvények segédfüggvénye. Segítségével meg tudjuk
     * nézni, hogy az építendő terület szomszédján van-e már út, hogy
     * lehessen építeni zónákat és épületeket. Ha nincs mellette hamissal
     * tér vissza és nem építhetünk.
     *
     * @param x
     * @param y
     * @return
     */
    private boolean canBuildR(int x, int y) {
        boolean road = false;
        for (Road r : ownsRoad) {
            int rx = r.getCoordinates().x;
            int ry = r.getCoordinates().y;
            if (rx == x + 1 && ry == y || rx == x - 1 && ry == y
                    || rx == x && ry == y + 1 || rx == x && ry == y - 1) {
                road = true;
            }
        }
        return road;
    }

    /**
     * A build() függvények segédfüggvénye. Segítségével
     * megvizsgálhatjuk, hogy kijelölt mezőn van-e már építve.
     *
     * @param x
     * @param y
     * @return igaz, ha üres a mező, hamis, ha már van rajta építve
     */
    private boolean canBuildB(int x, int y) {
        return city[x][y].compareTo("N/D") == 0;
    }

    /**
     * Kiindexelés vizsgálata út törlésének esetén, mivel ha van köré építve
     * zóna vagy épület, akkor nem törölhető.
     *
     * @param x
     * @param y
     * @return igaz, ha nincs köré építve zóna vagy épület, egyébként
     * hamis.
     */
    private boolean canDemolish(int x, int y) {
        if (y == w - 1) {
            if (x == 0) {
                return (city[x + 1][y].compareTo("N/D") == 0
                        || city[x + 1][y].compareTo("R") == 0)
                        && (city[x][y - 1].compareTo("N/D") == 0
                        || city[x][y - 1].compareTo("R") == 0);
            } else if (x + 1 == h) {
                return (city[x - 1][y].compareTo("N/D") == 0
                        || city[x - 1][y].compareTo("R") == 0)
                        && (city[x][y - 1].compareTo("N/D") == 0
                        || city[x][y - 1].compareTo("R") == 0);
            } else {
                return (city[x + 1][y].compareTo("N/D") == 0
                        || city[x + 1][y].compareTo("R") == 0)
                        && (city[x - 1][y].compareTo("N/D") == 0
                        || city[x - 1][y].compareTo("R") == 0)
                        && (city[x][y - 1].compareTo("N/D") == 0
                        || city[x][y - 1].compareTo("R") == 0);
            }
        } else if (y == 0) {
            if (x == 0) {
                return (city[x][y + 1].compareTo("N/D") == 0
                        || city[x][y + 1].compareTo("R") == 0)
                        && (city[x + 1][y].compareTo("N/D") == 0
                        || city[x + 1][y].compareTo("R") == 0);
            } else if (x + 1 == h) {
                return (city[x - 1][y].compareTo("N/D") == 0
                        || city[x - 1][y].compareTo("R") == 0)
                        && (city[x][y + 1].compareTo("N/D") == 0
                        || city[x][y + 1].compareTo("R") == 0);
            } else {
                return (city[x + 1][y].compareTo("N/D") == 0
                        || city[x + 1][y].compareTo("R") == 0)
                        && (city[x - 1][y].compareTo("N/D") == 0
                        || city[x - 1][y].compareTo("R") == 0)
                        && (city[x][y + 1].compareTo("N/D") == 0
                        || city[x][y + 1].compareTo("R") == 0);
            }
        } else if (y < w && y > 0) {
            if (x == 0) {
                return (city[x + 1][y].compareTo("N/D") == 0
                        || city[x + 1][y].compareTo("R") == 0)
                        && (city[x][y + 1].compareTo("N/D") == 0
                        || city[x][y + 1].compareTo("R") == 0)
                        && (city[x][y - 1].compareTo("N/D") == 0
                        || city[x][y - 1].compareTo("R") == 0);
            } else if (x == h - 1) {
                return (city[x - 1][y].compareTo("N/D") == 0
                        || city[x - 1][y].compareTo("R") == 0)
                        && (city[x][y + 1].compareTo("N/D") == 0
                        || city[x][y + 1].compareTo("R") == 0)
                        && (city[x][y - 1].compareTo("N/D") == 0
                        || city[x][y - 1].compareTo("R") == 0);
            } else {
                return (city[x + 1][y].compareTo("N/D") == 0
                        || city[x + 1][y].compareTo("R") == 0)
                        && (city[x - 1][y].compareTo("N/D") == 0
                        || city[x - 1][y].compareTo("R") == 0)
                        && (city[x][y + 1].compareTo("N/D") == 0
                        || city[x][y + 1].compareTo("R") == 0)
                        && (city[x][y - 1].compareTo("N/D") == 0
                        || city[x][y - 1].compareTo("R") == 0);
            }
        } else {
            return false;
        }
    }

    public int debt() {
        return fund < 0 ? -25 : 0;
    }

    /**
     * Ipari és szolgáló zóna aránya. Elégedettség vizsgálathoz szükséges.
     *
     * @return zónák mennyiségének a különbsége alapján egy elégedettségi mutató
     */
    public int ratio() {
        int countIndustrial = 0, countService = 0;
        for (Zone z : ownsZone) {
            if (z instanceof Industrial) {
                countIndustrial++;
            }
            if (z instanceof Service) {
                countService++;
            }
        }
        int difference = Math.abs(countService - countIndustrial);
        return difference > 10 ? -10 : -difference;
    }

    /**
     * Konzolra kiírhatjuk vele a flags és city mátrixokat.
     */
    public void printCity() {
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                System.out.print(i + "" + j + "  ");
            }
            System.out.println();
        }

        System.out.println();

        System.out.println("(oszlop,sor) = (x,y)");

        System.out.println("  0  1  2  3  4  5  6  7  8  9");
        for (int i = 0; i < w; i++) {
            System.out.print(i + " ");
            for (int j = 0; j < h; j++) {
                if (city[i][j].compareTo("N/D") == 0) {
                    System.out.print(".  ");
                } else {
                    System.out.print(city[i][j] + "  ");
                }
            }
            System.out.println();
        }

        System.out.println();

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (flags[i][j] < 10) {
                    System.out.print(flags[i][j] + "   ");
                } else {
                    System.out.print(flags[i][j] + "  ");
                }
            }
            System.out.println();
        }
    }

    // SETTER, GETTER, TOSTRING
    public int getFund() {
        return fund;
    }

    public ArrayList<Zone> getOwnsZone() {
        return ownsZone;
    }

    public ArrayList<Building> getOwnsBuilding() {
        return ownsBuilding;
    }

    public ArrayList<Road> getOwnsRoad() {
        return ownsRoad;
    }

    public ArrayList<Forest> getOwnsForest() {
        return ownsForest;
    }

    public Tax getTax() {
        return tax;
    }

    public String getCityAt(int x, int y) {
        return city[x][y];
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public void setFund(int fund) {
        this.fund = fund;
    }

    public void setTax(Tax tax) {
        this.tax = tax;
    }

    public int getFlagsXY(int x, int y) {
        return flags[x][y];
    }

    public void setFlagsXY(int x, int y, int value) {
        flags[x][y] = value;
    }

    // ID�?VEL KAPCSOLATOS TEVÉKENYSÉGEK
    public int getYear() {
        return year;
    }

    public Months getMonth() {
        return month;
    }

    public int getWeek() {
        return week;
    }

    public void timerStep() {
        weekStepNum++;
        if (weekStepNum >= SpeedOfTimeNum) {
            weekStepNum = 0;
            changeWeek();
        }
    }

    public void changeWeek() {
        week++;
        if (week == 5) {
            week = 1;
            changeMonth();
        }
        //things
        if (new Random().nextInt(100) - getCityHappiness() / 10 < 10) {
            moveInBuffer++;
        }
        if (moveInBuffer > 0 && !isCivilFull()) {
            int temp = new Random().nextInt(numberOfCivilZones());
            for (Zone zone : ownsZone) {
                if (zone instanceof Civil civil) {
                    if (temp == 0) {
                        if (civil.fullness() != 1) {
                            int randomAge = ThreadLocalRandom.current().nextInt(18, 60);
                            new Person(civil, new Industrial(-1, -1, this), this,  randomAge);
                            civil.setNumberOfPeople(civil.getNumberOfPeople() + 1);
                            civil.moveIn();
                            moveInBuffer--;
                        }
                    }
                    temp--;
                }
            }
        }
        if (numberOfCivilZones() > 0) {
            if (getRandomNumber(1, 3) == 1) {
                if (numberOfServiceZones() > 0) {
                    int temp = new Random().nextInt(numberOfCivilZones());
                    for (Zone zone : ownsZone) {
                        if (zone instanceof Civil civil) {
                            if (temp == 0) {
                                if (civil.getNumberOfPeople() > 0) {
                                    int temp3 = new Random().nextInt(civil.getNumberOfPeople());
                                    for (Person person : civil.getPeople()) {
                                        if (temp3 == 0) {
                                            if (!person.hasJob()) {
                                                int temp2 = new Random().nextInt(numberOfServiceZones());
                                                for (Zone servicezone : ownsZone) {
                                                    if (servicezone instanceof Service service) {
                                                        if (temp2 == 0) {
                                                            if (service.fullness() != 1) {
                                                                person.setWorkplace(service);
                                                                service.buildWorkplace();
                                                                service.setNumberOfPeople(service.getNumberOfPeople() + 1);
                                                            }
                                                        }
                                                        temp2--;
                                                    }
                                                }

                                            }
                                        }
                                        temp3--;

                                    }
                                }
                            }
                            temp--;
                        }
                    }
                }
            } else {
                if (numberOfIndustrialZones() > 0) {
                    int temp = new Random().nextInt(numberOfCivilZones());
                    for (Zone zone : ownsZone) {
                        if (zone instanceof Civil civil) {
                            if (temp == 0) {
                                if (civil.getNumberOfPeople() > 0) {
                                    for (Person person : civil.getPeople()) {
                                        if (!person.hasJob()) {
                                            int temp2 = new Random().nextInt(numberOfIndustrialZones());
                                            for (Zone industrialzone : ownsZone) {
                                                if (industrialzone instanceof Industrial industrial) {
                                                    if (temp2 == 0) {
                                                        if (industrial.fullness() != 1) {
                                                            person.setWorkplace(industrial);
                                                            industrial.buildWorkplace();
                                                            industrial.setNumberOfPeople(industrial.getNumberOfPeople() + 1);
                                                        }
                                                    }
                                                    temp2--;
                                                }
                                            }

                                        }
                                    }
                                }
                            }
                            temp--;
                        }
                    }
                }
            }

        }
    }

    public void changeMonth() {
        month = month.next();
        if (month == Months.JAN) {
            changeYear();
        }
        //things
        for (Zone zone : ownsZone) {
            if (zone instanceof Civil civil) {
                civil.payTax();
            }
        }
        moveOut();
        if (getCityHappiness() < 14) {
            criticalHappinessCounter = 0;
        } else {
            criticalHappinessCounter++;
        }
        if (criticalHappinessCounter >= 6) {
            GameOver();
        }
    }

    public void changeYear() {
        year++;
        ownsZone.forEach( zone -> {
            if (zone instanceof Civil civil){
                civil.getPeople().forEach(Person::ageUp);

                Predicate<Person> die = person -> {
                  if (person.isTaxPayer()){
                      return false;
                  }
                  int ageDiff = (person.getAge() - 65) * 10;
                  if (ageDiff >= 100){
                      ageDiff = 99;
                  }

                  boolean isDead = ThreadLocalRandom.current().nextInt(0, 100) < ageDiff;

                  if (isDead){
                      moveInBuffer++;
                  }

                  return isDead;
                };
                civil.getPeople().removeIf(
                        die
                );
            }
        });



        maintain();
        forestAging();
    }

    public void changeTimeSpeedTo1() {
        this.SpeedOfTimeNum = 6;
    }

    public void changeTimeSpeedTo2() {
        this.SpeedOfTimeNum = 3;
    }

    public void changeTimeSpeedTo3() {
        this.SpeedOfTimeNum = 2;
    }

    public boolean isCivilFull() {
        for (Zone zone : ownsZone) {
            if (zone instanceof Civil civil) {
                if (civil.fullness() != 1) {
                    return false;
                }
            }
        }
        return true;
    }

    public int getPopulation() {
        int n = 0;
        for (Zone zone : ownsZone) {
            if (zone instanceof Civil civil) {
                n += civil.getNumberOfPeople();
            }
        }
        return n;
    }

    public int getCityHappiness() {
        int n = 0;
        int q = 0;
        for (Zone zone : ownsZone) {
            if (zone instanceof Civil civil) {
                n += civil.calculateHappiness();
                q++;
            }
        }
        if (q == 0) {
            return 0;
        }
        return n / q;
    }

    public int numberOfCivilZones() {
        int n = 0;
        for (Zone zone : ownsZone) {
            if (zone instanceof Civil) {
                n++;
            }
        }
        return n;
    }

    public int numberOfServiceZones() {
        int n = 0;
        for (Zone zone : ownsZone) {
            if (zone instanceof Service) {
                n++;
            }
        }
        return n;
    }

    public int numberOfIndustrialZones() {
        int n = 0;
        for (Zone zone : ownsZone) {
            if (zone instanceof Industrial) {
                n++;
            }
        }
        return n;
    }

    public void moveOut() {
        for (Zone zone : ownsZone) {
            if (zone instanceof Civil civil) {
                civil.getPeople().removeIf(person -> person.calculateHappiness() < 2);
                civil.setNumberOfPeople(civil.getPeople().size());
                civil.moveIn();
            }
        }
    }

    public void forestAging() {
        for (Forest forest : ownsForest) {
            forest.ageUp();
        }
    }

    public int getRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    public void GameOver() {

    }

    @Override
    public String toString() {
        return "Mayor: " + fund + " " + ownsRoad.toString() + " ";
    }

    public void setSave(Save save) {
        this.fund = save.money();
        this.year = save.year();
        this.month = Months.values()[save.month() - 1];
        this.week = save.week();
    }
}
