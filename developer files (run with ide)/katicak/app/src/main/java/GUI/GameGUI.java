
package GUI;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import SQL.SQLDatabase;
import SQL.Save;
import model.Mayor;
import model.Tax;
import javax.swing.Timer;

public class GameGUI extends JFrame{
    private final JFrame frame;
    private final BoardGUI boardGUI;
    private final Timer timer;
    private final int STEP_TIME = 150;

    private final int INITIAL_BOARD_SIZE = 10;

    private final JMenu loadgameMenu;
    private final JMenu deletegameMenu;

    public boolean isPaused = false;
    public GameGUI() {
        frame = new JFrame("Sim city");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        boardGUI = new BoardGUI(INITIAL_BOARD_SIZE);
        frame.getContentPane().add(boardGUI.getLabel(), BorderLayout.NORTH);
        frame.getContentPane().add(boardGUI.getBoardPanel(), BorderLayout.CENTER);

        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        JMenu gameMenuGame = new JMenu("Game");
        menuBar.add(gameMenuGame);

        JMenuItem newGameMenuItem = new JMenuItem("New game");
        gameMenuGame.add(newGameMenuItem);
        newGameMenuItem.addActionListener(ae -> System.out.println("new game"));

        JMenuItem saveMenuItem = new JMenuItem("Save game");
        gameMenuGame.add(saveMenuItem);
        saveMenuItem.addActionListener(new SaveActionListener(boardGUI, "save.db", this));


        loadgameMenu = new JMenu("Load game");
        gameMenuGame.add(loadgameMenu);


        deletegameMenu = new JMenu("Delete game");
        gameMenuGame.add(deletegameMenu);

        loadSaves();

        JMenuItem exitMenuItem = new JMenuItem("Exit");
        gameMenuGame.add(exitMenuItem);
        exitMenuItem.addActionListener(ae -> System.exit(0));

        JMenu timeMenuGame = new JMenu("Game speed");
        menuBar.add(timeMenuGame);

        JMenuItem normalMenuItem = new JMenuItem("1x");
        timeMenuGame.add(normalMenuItem);
        normalMenuItem.addActionListener(ae -> {
            boardGUI.getMayor().changeTimeSpeedTo1();
            System.out.println("game speed: 1");
        });

        JMenuItem fastMenuItem = new JMenuItem("2x");
        timeMenuGame.add(fastMenuItem);
        fastMenuItem.addActionListener(ae -> {
            boardGUI.getMayor().changeTimeSpeedTo2();
            System.out.println("game speed: 2");
        });
        JMenuItem fasterMenuItem = new JMenuItem("3x");
        timeMenuGame.add(fasterMenuItem);
        fasterMenuItem.addActionListener(ae -> {
            boardGUI.getMayor().changeTimeSpeedTo3();
             System.out.println("game speed: 3");
        });

        JMenu gameMenuConstruction = new JMenu("Construction");
        menuBar.add(gameMenuConstruction);


        JMenuItem demolishMenuItem = new JMenuItem("Demolish");
        gameMenuConstruction.add(demolishMenuItem);
        demolishMenuItem.addActionListener(ae -> boardGUI.setSelected_item(0));

        JMenuItem roadMenuItem = new JMenuItem("Road");
        gameMenuConstruction.add(roadMenuItem);
        roadMenuItem.addActionListener(ae -> boardGUI.setSelected_item(10));

        JMenuItem policeMenuItem = new JMenuItem("Police station");
        gameMenuConstruction.add(policeMenuItem);
        policeMenuItem.addActionListener(ae -> boardGUI.setSelected_item(11));

        JMenuItem stadionMenuItem = new JMenuItem("Stadion");
        gameMenuConstruction.add(stadionMenuItem);
        stadionMenuItem.addActionListener(ae -> boardGUI.setSelected_item(12));

        JMenuItem upgradeMenuItem = new JMenuItem("Upgrade");
        gameMenuConstruction.add(upgradeMenuItem);
        upgradeMenuItem.addActionListener(ae -> boardGUI.setSelected_item(18));




        JMenu gameMenuZone = new JMenu("Zone");
        menuBar.add(gameMenuZone);

        JMenuItem residentialMenuItem = new JMenuItem("Residential zone");
        gameMenuZone.add(residentialMenuItem);
        residentialMenuItem.addActionListener(ae -> boardGUI.setSelected_item(1));

        JMenuItem commercialMenuItem = new JMenuItem("Commercial zone");
        gameMenuZone.add(commercialMenuItem);
        commercialMenuItem.addActionListener(ae -> boardGUI.setSelected_item(4));

        JMenuItem industrialMenuItem = new JMenuItem("Industrial zone");
        gameMenuZone.add(industrialMenuItem);
        industrialMenuItem.addActionListener(ae -> boardGUI.setSelected_item(7));



        JMenu gameMenuPlanting = new JMenu("Planting");
        menuBar.add(gameMenuPlanting);

        JMenuItem treeMenuItem = new JMenuItem("Tree");
        gameMenuPlanting.add(treeMenuItem);
        treeMenuItem.addActionListener(ae -> boardGUI.setSelected_item(16));

        JMenu gameMenuTax = new JMenu("Tax");
        menuBar.add(gameMenuTax);

        JMenuItem lowMenuItem = new JMenuItem("Low");
        gameMenuTax.add(lowMenuItem);
        lowMenuItem.addActionListener(ae -> boardGUI.getMayor().setTax(Tax.LOW));
        
        JMenuItem highMenuItem = new JMenuItem("High");
        gameMenuTax.add(highMenuItem);
        highMenuItem.addActionListener(ae -> boardGUI.getMayor().setTax(Tax.HIGH));


        JMenuItem clearMenuItem = new JMenuItem("Clear selected item");
        menuBar.add(clearMenuItem);
        clearMenuItem.addActionListener(ae -> boardGUI.setSelected_item(-1));


        timer = new Timer(STEP_TIME, ae -> {
            if (!isPaused){
                boardGUI.getMayor().timerStep();
                boardGUI.refreshGameStatLabel();
            }
        });
        timer.start();
        setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
    }

    public void loadSaves() {
        loadgameMenu.removeAll();
        deletegameMenu.removeAll();

        try(SQLDatabase sqlDatabase = new SQLDatabase("save.db")){
            ArrayList<Save> saves = sqlDatabase.getAllSaves();
            for (Save save : saves){

                JMenuItem loadMenuItemSave = new JMenuItem(save.save_name());
                loadgameMenu.add(loadMenuItemSave);
                loadMenuItemSave.addActionListener(new LoadActionListener(save, boardGUI, "save.db"));

                JMenuItem loadMenuItemDelete = new JMenuItem(save.save_name());
                deletegameMenu.add(loadMenuItemDelete);
                loadMenuItemDelete.addActionListener(new DeleteActionListener(save,this,  "save.db"));
            }

        } catch (SQLException | IOException exception){
            System.out.println(exception.getMessage());
        }
    }
}
