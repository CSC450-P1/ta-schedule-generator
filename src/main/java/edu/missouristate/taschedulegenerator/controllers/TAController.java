package edu.missouristate.taschedulegenerator.controllers;

import edu.missouristate.taschedulegenerator.SceneManager;
import edu.missouristate.taschedulegenerator.domain.TA;
import edu.missouristate.taschedulegenerator.SceneManager.Controller;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class TAController implements Controller<String>{
    @FXML private TextField taName;
    @FXML private TextField maxHours;
    @FXML private ToggleGroup isGA;
    @FXML private TableView timeblockTable;

    @Override
    public void initData(String data) {
        // This is where you would process data before showing the view
        taName.setText(data);
    }

    @FXML
    public void cancel(ActionEvent event) {
        // This is an example of how to switch scenes without passing data
        SceneManager.showScene("dashboard");
    }

    @FXML
    public void submitTAInfo(ActionEvent event) {
        TA newTA = new TA();

        newTA.setName(taName.getText());

        String maxHoursField = maxHours.getText();
        newTA.setMaxHours(Integer.parseInt(maxHoursField));

        RadioButton isGAField = (RadioButton) isGA.getSelectedToggle();
        if (isGAField.getText() == "No"){
            newTA.setGA(false);
        }else{
            newTA.setGA(true);
        }

        ObservableList<TableColumn> columns = timeblockTable.getColumns();
    }
}
