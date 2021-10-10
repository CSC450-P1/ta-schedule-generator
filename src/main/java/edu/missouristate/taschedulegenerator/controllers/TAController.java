package edu.missouristate.taschedulegenerator.controllers;

import edu.missouristate.taschedulegenerator.SceneManager;
import edu.missouristate.taschedulegenerator.domain.TA;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public class TAController {
    @FXML private TextField taName;
    @FXML private TextField maxHours;
    @FXML private ToggleGroup isGA;

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
    }
}
