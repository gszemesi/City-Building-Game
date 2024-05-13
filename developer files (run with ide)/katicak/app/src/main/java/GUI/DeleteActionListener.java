package GUI;

import SQL.SQLDatabase;
import SQL.Save;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;

public class DeleteActionListener implements ActionListener {

    private final Save save;
    private final GameGUI gameGUI;
    private final String filename;

    public DeleteActionListener(Save save, GameGUI boardGUI, String filename){
        this.save = save;
        this.gameGUI = boardGUI;
        this.filename = filename;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        try(SQLDatabase sqlDatabase = new SQLDatabase(filename)){
            sqlDatabase.deleteSave(save.id());
        } catch (SQLException | IOException exception){
            System.err.println(exception.getMessage());
        }

        gameGUI.loadSaves();
    }
}
