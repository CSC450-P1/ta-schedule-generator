package edu.missouristate.taschedulegenerator.controllers;

import edu.missouristate.taschedulegenerator.domain.TimeBlock;
import edu.missouristate.taschedulegenerator.util.SceneManager;
import edu.missouristate.taschedulegenerator.domain.TA;
import edu.missouristate.taschedulegenerator.util.SceneManager.Controller;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TAController implements SceneManager.Controller<String> {
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
    public void addActivity(ActionEvent event) {
        // This is an example of how to switch scenes and pass data to the new scene's controller to process before showing
        SceneManager.showScene("activityInfo");
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
        for (int i = 0; i < columns.size(); i++){
            TimeBlock timeBlock = new TimeBlock();
            List<TimeBlock> timeblocks = new ArrayList<TimeBlock>();
            TableColumn currCol = columns.get(i);
            Map<Object, Object> values = currCol.getProperties();

        }
    }
}
