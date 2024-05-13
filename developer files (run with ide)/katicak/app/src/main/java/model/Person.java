package model;

import java.awt.*;

public class Person {

    private final Civil home;
    private Zone workplace; // vagy Industrial vagy Service
    private int distanceFromWorkPlace;
    private int distanceFromIndustrial;
    private final int happiness;
    private final Mayor m;
    private int age = 18;

    public Person(Civil home, Mayor m) {
        this.home = home;
        //this.workplace = workplace; // ezt majd egy segédfüggvénnyel (pl. moveIn()) kéne meghatározni
        this.happiness = 0;
        this.m = m;
        home.getPeople().add(this);
        this.workplace = new Industrial(-1, -1, m);
    }

    // Teszteléshez
    public Person(Civil home, Zone workplace, Mayor m) {
        this.home = home;
        this.workplace = workplace;
        this.happiness = 0;
        this.m = m;
        home.getPeople().add(this);
    }

    public Person(Civil home, Zone workplace, Mayor mayor, int age){
        this.home = home;
        this.workplace = workplace;
        this.happiness = 0;
        this.m = mayor;
        home.getPeople().add(this);
        this.age = age;
    }

    public int ratio() { return m.ratio(); }
    public int debt() { return m.debt(); }
    public int tax() { return m.getTax() == Tax.LOW ? 15 : -15; }

    /**
     * Megkeresi, hogy van-e munkahelyhez és lakáshoz közeli rendőrség 8
     * mezőnyire, és ha van, akkor telítettségtől függően kiszámít egy
     * elégedettséget.
     * @return safety
     */
    public int safety() {
        boolean hasPoliceH = false, hasPoliceW = false;
        int minStepH = m.getW() + m.getH(), minStepW = m.getW() + m.getH();
        for (int i = 0; i < m.getOwnsBuilding().size(); i++) {
            if (m.getOwnsBuilding().get(i) instanceof Police p) {
                int currStepH = Math.abs(p.getCoordinates().x - home.getCoordinates().x)
                        + Math.abs(p.getCoordinates().y - home.getCoordinates().y);
                int currStepW = Math.abs(p.getCoordinates().x - workplace.getCoordinates().x)
                        + Math.abs(p.getCoordinates().y - workplace.getCoordinates().y);

                if (minStepH > currStepH) {
                    hasPoliceH = true;
                    minStepH = currStepH;
                }
                if (minStepW > currStepW) {
                    hasPoliceW = true;
                    minStepW = currStepW;
                }
            }
        }

        if (hasPoliceH) hasPoliceH = minStepH <= 8;
        if (hasPoliceW) hasPoliceW = minStepW <= 8;
        int safety = 0;

        if (hasPoliceH && home.fullness() > 0.6) safety += 13;
        else if (!hasPoliceH && home.fullness() > 0.6) safety -= 13;
        else if (hasPoliceH && home.fullness() <= 0.6) safety += 8;
        else if (!hasPoliceH && home.fullness() <= 0.6) safety -= 8;
        
        if(hasJob()){
            if (hasPoliceW && workplace.fullness() > 0.6) safety += 12;
            else if (!hasPoliceW && workplace.fullness() > 0.6) safety -= 12;
            else if (hasPoliceW && workplace.fullness() <= 0.6) safety += 7;
            else if (!hasPoliceW && workplace.fullness() <= 0.6) safety -= 7;
        }
        

        return safety;
    }

    /**
     * Megkeresi, hogy van-e 5 lépésnyire otthontól vagy munkahelytől stadion.
     *
     * @return 15, ha van munkahely vagy otthon közelében, egyébként 0
     */
    public int stadion() {
        boolean hasStadiumH = false, hasStadiumW = false;
        int minStepH = m.getW() + m.getH(), minStepW = m.getW() + m.getH();
        for (Building building : m.getOwnsBuilding()) {
            if (building instanceof Stadium s) {
                int currStepH = Math.abs(s.getCoordinates().x - home.getCoordinates().x)
                        + Math.abs(s.getCoordinates().y - home.getCoordinates().y);
                int currStepW = Math.abs(s.getCoordinates().x - workplace.getCoordinates().x)
                        + Math.abs(s.getCoordinates().y - workplace.getCoordinates().y);

                if (minStepH > currStepH) {
                    hasStadiumH = true;
                    minStepH = currStepH;
                }
                if (minStepW > currStepW) {
                    hasStadiumW = true;
                    minStepW = currStepW;
                }
            }
        }

        if (hasStadiumH) hasStadiumH = minStepH <= 5;
        if (hasStadiumW) hasStadiumW = minStepW <= 5 && hasJob();

        return hasStadiumW || hasStadiumH ? 15 : 0;
    }

    public boolean hasJob() {
        return !workplace.getCoordinates().equals(new Point(-1, -1));
    }
    
    public int jobless(){ return hasJob() ? -15 : 0; }

    /**
     * Megkeresi otthonhoz a legközelebbi erdőt.
     * @return erdő korával arányosan visszatér egy értékkel. Ha rálát lakó
     * az otthonából, akkor +10 ponttal tér vissza (max. 20), ha nem lát rá,
     * akkor +5 ponttal tér vissza (max. 15).
     */
    public int forest() {
        boolean hasForest = false;
        int minStep = m.getW() + m.getH();
        Forest minF = new Forest(-1, -1, m);
        for (Forest f : m.getOwnsForest()) {
            int currStep = distance(f.getCoordinates().x, f.getCoordinates().y,
                    home.getCoordinates().x, home.getCoordinates().y);

            if (minStep > currStep && currStep<=3) {
                hasForest = true;
                minStep = currStep;
                minF = f;
            }
        }

        int x1 = 0, x2 = 0, y1 = 0, y2 = 0;
        if (home.getCoordinates().x <= minF.getCoordinates().x) {
            x1 = home.getCoordinates().x;
            x2 = minF.getCoordinates().x;
        } else {
            x1 = minF.getCoordinates().x;
            x2 = home.getCoordinates().x;
        }
        if (home.getCoordinates().y <= minF.getCoordinates().y) {
            y1 = home.getCoordinates().y;
            y2 = minF.getCoordinates().y;
        } else {
            y1 = minF.getCoordinates().y;
            y2 = home.getCoordinates().y;
        }

        if (hasForest) {
            boolean canSee = false, industrial = false;
            for (int x = x1; x <= x2; x++) {
                for (int y = y1; y <= y2; y++) {
                    if (distance(x1, y1, x2, y2) == 1) {
                        canSee = true;
                    }
                    if (!(x == home.getCoordinates().x && y == home.getCoordinates().y)
                            && !(x == minF.getCoordinates().x && y == minF.getCoordinates().y)) {
                        //Ha csak üres vagy Road van köztük, akkor rá lehet látni
                        canSee = (m.getCityAt(x, y).compareTo("N/D") == 0
                                || m.getCityAt(x, y).compareTo("R") == 0);
                        if (!canSee && m.getFlagsXY(x, y) == 7) industrial = true;

                    }

                }
            }
            if (industrial) return Math.min(minF.getAge() + 5, 15);
            else if (canSee) return Math.min(minF.getAge() + 10, 20);
        }
        return 0;
    }

    private int distance(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    /**
     * Legközelebbi ipari zóna milyen hatással van személy boldogságára.
     *
     * @return
     */
    public int getDistanceFromIndustrial() {
        int distance = (int) Math.floor(Math.sqrt(Math.pow(m.getW() - 1, 2) + Math.pow(m.getH() - 1, 2)));
        for (Zone z : m.getOwnsZone()) {
            if (z instanceof Industrial i) {
                Point p1 = i.getCoordinates();
                int x1 = p1.x;
                int y1 = p1.y;
                Point p2 = home.getCoordinates();
                int x2 = p2.x;
                int y2 = p2.y;

                int d = (int) Math.floor(Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)));
                if (d < distance) {
                    distance = d;
                }
            }
        }
        return distance;
    }

    public int commute() {
        if(hasJob()){
            int step = Math.abs(home.getCoordinates().x - workplace.getCoordinates().x)
                    + Math.abs(home.getCoordinates().y - workplace.getCoordinates().y);

            if (step <= 2) return 15;
            else if (step <= 4) return 10;
            else if (step <= 6) return 5;
            else if (step > 6) return -15;
        }
        return 0;
    }

    /**
     * Segédfüggvények alapján kiszámolja, hogy mennyire elégedett egy személy
     * (erdő, stadion, rendőrség, adó, zónák aránya, adósság, munkanélküliség,
     * munkahely közelsége, ipari zóna közelsége).
     *
     * @return Segédfüggvények összege. Ha ez meghaladja a 100-at, akkor 100 lesz,
     * ha kisebb, mint 0, akkor 0 lesz.
     */
    public int calculateHappiness() { //kivettem a commute �s jobless fv() eket am�g kital�ljuk hogy lesz a job
        int happiness = 0;

        if (isTaxPayer()){
            happiness = safety() + commute() + home.industrialEffect() + tax() +
                    debt() + jobless() + ratio() + forest() + stadion();
        } else {
            happiness = safety() +  home.industrialEffect() + tax() +  debt() + ratio() + forest() + stadion();
        }
        /*System.out.println(safety()+" "+ commute() +" "+ home.industrialEffect() +" "+ tax() +" "+
                        debt() +" "+ jobless() + " "+ratio() +" "+ forest() +" "+ stadion());*/

        if (happiness > 100) {
            happiness = 100;
        } else if (happiness < 0) {
            happiness = 0;
        }

        return happiness;
    }

    public void setWorkplace(Zone z) {
        this.workplace = z;
    }

    /**
     * El tud-e jutni rendőrségre, stadionba, munkahelyére. Lehet hogy
     * felesleges és okosabban is meg lehet csinálni?
     *
     * @param x
     * @param y
     * @return
     */
    public boolean canVisit(int x, int y) {
        return false;
    }

    public int getHappiness() {
        return happiness;
    }

    public Civil getHome() {
        return home;
    }

    public Zone getWorkplace() {
        return workplace;
    }

    public void ageUp(){
        ++age;

        if (age > 65){
            workplace = new Industrial(-1, -1, m);
        }
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isTaxPayer(){
        return age < 65;
    }
}
