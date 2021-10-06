package edu.missouristate.taschedulegenerator.controllers;

import edu.missouristate.taschedulegenerator.SceneManager;
import edu.missouristate.taschedulegenerator.SceneManager.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public class TAController implements Controller<String> {
	
	@FXML
	private TextField TAName;
	
	@FXML
	private TextField MaxHoursPerWeek;
	
	@FXML
	private RadioButton YesGAButton;
	
	@FXML
	private RadioButton NoGAButton;
	
	@FXML
	private ToggleGroup isGA;

	@Override
	public void initData(String data) {
		// TODO Auto-generated method stub
		
	}
	
	
	@FXML
	public void cancel(ActionEvent event) {
		SceneManager.showScene("dashboard");
	}
	
	@FXML
	public void saveTAInfo(ActionEvent event) {
		
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Warning");
		alert.setHeaderText("Invalid Data Entry");
		//String contentText = "";
		if(!TAName.getText().matches("[a-zA-Z ]+")) {
			alert.setContentText("Please confirm that '" + TAName.getText() + "' only contains alphabetic characters.");
			alert.showAndWait();
		}
		if(!isNumeric(MaxHoursPerWeek.getText())) {
			alert.setContentText("Please confirm that '" + MaxHoursPerWeek.getText() + "' only contains numeric characters.");
			alert.showAndWait();
		}
		//If user selects both options
		if(YesGAButton.isSelected() && NoGAButton.isSelected()) {
			alert.setContentText("Please only select one option if the TA is a GA.");
			alert.showAndWait();
		//If user does not select any option
		} else if (YesGAButton.isSelected() == false && NoGAButton.isSelected() == false) {
			alert.setContentText("Please select whether the TA is also a GA.");
			alert.showAndWait();
		}
		
		
	
	}

	//Function to assist in data validation of the MaxHoursPerWeek TextField contents
	private static Boolean isNumeric(String strNum) {
		if (strNum == null) {
	        return false;
	    }
	    try {
	        double d = Double.parseDouble(strNum);
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}
}
