package GUI;

import model.*;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class BoardGUI {

    private final int boardSize;
    private final JLabel Label;
    private final JPanel boardPanel;
    private final JButton[][] buttons;
    private int selected_item = -1;

    private Mayor Mayor;

    private final ImageIcon iconField;
    private final ImageIcon iconResidentialZone;
    private final ImageIcon iconResidentialBuildingSmall;
    private final ImageIcon iconResidentialBuildingLarge;
    private final ImageIcon iconCommercialZone;
    private final ImageIcon iconCommercialBuildingSmall;
    private final ImageIcon iconCommercialBuildingLarge;
    private final ImageIcon iconIndustrialZone;
    private final ImageIcon iconIndustrialBuildingSmall;
    private final ImageIcon iconIndustrialBuildingLarge;
    private final ImageIcon iconRoad;
    private final ImageIcon iconPoliceStation;
    private final ImageIcon iconStadion1;
    private final ImageIcon iconStadion2;
    private final ImageIcon iconStadion3;
    private final ImageIcon iconStadion4;
    private final ImageIcon iconSapling;
    private final ImageIcon iconTree;
    private final ImageIcon iconResidentialBuildingFirstUpgrade;
    private final ImageIcon iconResidentialBuildingSecondUpgrade;
    private final ImageIcon iconServiceBuildingFirstUpgrade;
    private final ImageIcon iconServiceBuildingSecondUpgrade;
    private final ImageIcon iconIndustrialBuildingFirstUpgrade;
    private final ImageIcon iconIndustrialBuildingSecondUpgrade;

    public BoardGUI(int boardSize) {
        this.boardSize = boardSize;

        Mayor = new Mayor(Tax.LOW);

        iconField = new ImageIcon("./icons/field.png");
        iconResidentialZone = new ImageIcon("icons/residential zone.png");
        iconResidentialBuildingSmall = new ImageIcon("icons/residential_building_small.png");
        iconResidentialBuildingLarge = new ImageIcon("icons/residential_building_large.png");
        iconCommercialZone = new ImageIcon("icons/commercial zone.png");
        iconCommercialBuildingSmall = new ImageIcon("icons/commercial_building_small.png");
        iconCommercialBuildingLarge = new ImageIcon("icons/commercial_building_large.png");
        iconIndustrialZone = new ImageIcon("icons/industrial zone.png");
        iconIndustrialBuildingSmall = new ImageIcon("icons/industrial_building_small.png");
        iconIndustrialBuildingLarge = new ImageIcon("icons/industrial_building_large.png");
        iconRoad = new ImageIcon("./icons/road.png");
        iconPoliceStation = new ImageIcon("icons/police station.png");
        iconStadion1 = new ImageIcon("icons/stadion1.png");
        iconStadion2 = new ImageIcon("icons/stadion2.png");
        iconStadion3 = new ImageIcon("icons/stadion3.png");
        iconStadion4 = new ImageIcon("icons/stadion4.png");
        iconSapling = new ImageIcon("icons/sapling.png");
        iconTree = new ImageIcon("icons/tree.png");
        iconResidentialBuildingFirstUpgrade = new ImageIcon("icons/residential_15.png");
        iconResidentialBuildingSecondUpgrade = new ImageIcon("icons/residential_20.png");
        iconServiceBuildingFirstUpgrade = new ImageIcon("icons/service_7.png");
        iconServiceBuildingSecondUpgrade = new ImageIcon("icons/service_10.png");
        iconIndustrialBuildingFirstUpgrade = new ImageIcon("icons/industrial_7.png");
        iconIndustrialBuildingSecondUpgrade = new ImageIcon("icons/industrial_10.png");

        Label = new JLabel("Sim city");
        Label.setHorizontalAlignment(JLabel.LEFT);
        refreshGameStatLabel();

        boardPanel = new JPanel();

        boardPanel.setLayout(new GridLayout(boardSize, boardSize));
        buttons = new JButton[boardSize][boardSize];

        for (int i = 0; i < boardSize; ++i) {
            for (int j = 0; j < boardSize; ++j) {

                JButton button = new JButton();
                button.addMouseListener(new ButtonListener(i, j));
                button.setPreferredSize(new Dimension(50, 50));
                buttons[i][j] = button;
                boardPanel.add(button);
            }
        }

        refresh();
    }

    public void refresh() {
        for (int i = 0; i < boardSize; ++i) {
            for (int j = 0; j < boardSize; ++j) {
                JButton button = buttons[i][j];
                try {
                    switch (Mayor.getFlagsXY(i, j)) {
                        case 0 ->
                            button.setIcon(iconField);
                        case 1 ->
                            button.setIcon(iconResidentialZone);
                        case 2 ->
                            button.setIcon(iconResidentialBuildingSmall);
                        case 3 ->
                            button.setIcon(iconResidentialBuildingLarge);
                        case 4 ->
                            button.setIcon(iconCommercialZone);
                        case 5 ->
                            button.setIcon(iconCommercialBuildingSmall);
                        case 6 ->
                            button.setIcon(iconCommercialBuildingLarge);
                        case 7 ->
                            button.setIcon(iconIndustrialZone);
                        case 8 ->
                            button.setIcon(iconIndustrialBuildingSmall);
                        case 9 ->
                            button.setIcon(iconIndustrialBuildingLarge);
                        case 10 ->
                            button.setIcon(iconRoad);
                        case 11 ->
                            button.setIcon(iconPoliceStation);
                        case 12 ->
                            button.setIcon(iconStadion1);
                        case 13 ->
                            button.setIcon(iconStadion3);
                        case 14 ->
                            button.setIcon(iconStadion2);
                        case 15 ->
                            button.setIcon(iconStadion4);
                        case 16 ->
                            button.setIcon(iconSapling);
                        case 17 ->
                            button.setIcon(iconTree);
                        case 21 ->
                                button.setIcon(iconResidentialBuildingFirstUpgrade);
                        case 31 ->
                                button.setIcon(iconResidentialBuildingSecondUpgrade);
                        case 51 ->
                                button.setIcon(iconServiceBuildingFirstUpgrade);
                        case 61 ->
                                button.setIcon(iconServiceBuildingSecondUpgrade);
                        case 81 ->
                                button.setIcon(iconIndustrialBuildingFirstUpgrade);
                        case 91 ->
                                button.setIcon(iconIndustrialBuildingSecondUpgrade);
                        default ->
                            throw new Exception("Refresh error!");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void setMayor(model.Mayor mayor) {
        Mayor = mayor;
    }

    public class ButtonListener implements MouseListener {

        private final int x, y;

        public ButtonListener(int x, int y) {
            this.x = x;
            this.y = y;

        }

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e
        ) {
            try {
                switch (selected_item) {
                    case -1 -> showInfo();
                    case 0 ->
                        Mayor.demolish(x, y);
                    case 1 ->
                        Mayor.buildCivil(x, y);
                    case 4 ->
                        Mayor.buildService(x, y);
                    case 7 ->
                        Mayor.buildIndustrial(x, y);
                    case 10 ->
                        Mayor.buildRoad(x, y);
                    case 11 ->
                        Mayor.buildPolice(x, y);
                    case 12 ->
                        Mayor.buildStadium(x, y);
                    case 16 ->
                        Mayor.plantForest(x, y);
                    case 18 ->
                        Mayor.upgrade(x,y);
                    default ->
                        throw new Exception("Building error!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e
        ) {

        }

        @Override
        public void mouseEntered(MouseEvent e
        ) {
            JButton button1 = buttons[x][y];
            try {
                switch (selected_item) {
                    case -1 -> {}
                    case 0 -> button1.setIcon(iconField);
                    case 1 -> button1.setIcon(iconResidentialZone);
                    case 4 -> button1.setIcon(iconCommercialZone);
                    case 7 -> button1.setIcon(iconIndustrialZone);
                    case 10 -> button1.setIcon(iconRoad);
                    case 11 -> button1.setIcon(iconPoliceStation);
                    case 12 -> {
                        if (x + 1 < boardSize && y + 1 < boardSize) {
                            JButton button2 = buttons[x][y + 1];
                            JButton button3 = buttons[x + 1][y];
                            JButton button4 = buttons[x + 1][y + 1];
                            button1.setIcon(iconStadion1);
                            button2.setIcon(iconStadion2);
                            button3.setIcon(iconStadion3);
                            button4.setIcon(iconStadion4);
                        }
                    }
                    case 16 -> button1.setIcon(iconSapling);
                    //default -> throw new RuntimeException("Hover error!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void mouseExited(MouseEvent e
        ) {
            if (selected_item != -1) {
                refresh();
            }
        }

        private void showInfo(){
            int flag = Mayor.getFlagsXY(x, y);
            switch (flag) {
                case 1, 2, 3 -> showCivilZoneInfo();
                case 4, 5, 6 -> showServiceZoneInfo();
                case 7, 8, 9 -> showIndustrialZoneInfo();
                case 10 -> showRoad();
                case 11 -> showPolice();
                case 12, 13, 14, 15 -> showStadion();
                case 16, 17 -> showTree();

                default -> JOptionPane.showMessageDialog(boardPanel, Mayor.getFlagsXY(x, y), "Area data", JOptionPane.PLAIN_MESSAGE);

            }
        }

        private void showTree() {
            var Tree = Mayor.getOwnsForest()
                    .stream().filter(zone -> zone.getCoordinates().equals(new Point(x, y))).findFirst();

            var tree = Tree.orElseThrow();
            JOptionPane.showMessageDialog(boardPanel, tree.getAge(), "Tree", JOptionPane.PLAIN_MESSAGE);
        }

        private void showStadion() {
            JOptionPane.showMessageDialog(boardPanel, Mayor.getFlagsXY(x, y), "Stadion", JOptionPane.PLAIN_MESSAGE);
        }

        private void showPolice() {
            JOptionPane.showMessageDialog(boardPanel, Mayor.getFlagsXY(x, y), "Police", JOptionPane.PLAIN_MESSAGE);

        }

        private void showRoad() {
            JOptionPane.showMessageDialog(boardPanel, Mayor.getFlagsXY(x, y), "Road", JOptionPane.PLAIN_MESSAGE);
        }

        private void showIndustrialZoneInfo() {
            var Zone = Mayor.getOwnsZone()
                    .stream().filter(zone -> zone.getCoordinates().equals(new Point(x, y))).findFirst();

            var industrial = (Industrial)Zone.orElseThrow();
            JOptionPane.showMessageDialog(boardPanel, "Workers: "+industrial.getNumberOfPeople(), "Industrial Zone", JOptionPane.PLAIN_MESSAGE);
        }

        private void showServiceZoneInfo() {
            var Zone = Mayor.getOwnsZone()
                    .stream().filter(zone -> zone.getCoordinates().equals(new Point(x, y))).findFirst();

            var service = (Service)Zone.orElseThrow();
            JOptionPane.showMessageDialog(boardPanel, "Workers: "+service.getNumberOfPeople(), "Service Zone", JOptionPane.PLAIN_MESSAGE);
        }

        private void showCivilZoneInfo() {

            var zones = Mayor.getOwnsZone()
                    .stream().filter(zone -> zone.getCoordinates().equals(new Point(x, y))).findFirst();
            var civil = (Civil)zones.orElseThrow();
            var stat = civil.getPeople().stream().mapToInt(Person::calculateHappiness).summaryStatistics();
            var statAge = civil.getPeople().stream().mapToInt(Person::getAge).summaryStatistics();
            int taxPayers = (int)civil.getPeople().stream().filter(Person::isTaxPayer).count();
            int notTaxPayer = civil.getPeople().size() - taxPayers;
            int hasJob = (int) civil.getPeople().stream().filter(Person::hasJob).count();
            String Message = String.format("""
                    Residents = %d
                    Avg happines = %d
                    Min happines = %d
                    Max happines = %d
                    Avg age = %f
                    Min age = %d
                    Max age = %d
                    Tax Payer = %d
                    Not Tax Payer = %d
                    Has Job = %d
                    """,
                    stat.getCount(), (int)stat.getAverage(), stat.getMin(), stat.getMax() ,
                    statAge.getAverage(), statAge.getMin(), statAge.getMax(),
                    taxPayers, notTaxPayer, hasJob);
            JOptionPane.showMessageDialog(boardPanel, Message, "Civil Zone", JOptionPane.PLAIN_MESSAGE);
        }

    }

    public JPanel getBoardPanel() {
        return boardPanel;
    }

    public JLabel getLabel() {
        return Label;
    }

    public int getSelected_item() {
        return selected_item;
    }

    public void setSelected_item(int selected_item) {
        this.selected_item = selected_item;
    }

    public Mayor getMayor() {
        return Mayor;
    }

    public void refreshGameStatLabel() {
        String s = "Funds: " + Mayor.getFund() + "$  ";
        s += "    Population: "+Mayor.getPopulation()+"       Happiness: "+Mayor.getCityHappiness()+"        Date: " + Mayor.getYear() + " " + Mayor.getMonth() + " ,week " + Mayor.getWeek();
        Label.setText(s);
    }
}
