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
		alert.setHeaderText("Possible Invalid Data Entry");
		//ButtonType type = new ButtonType("Ok", ButtonData.OK_DONE);
		//alert.setContentText("Please confirm that ");
		
		if(!TAName.getText().matches("[a-zA-Z ]+")) {
			alert.setContentText("Please confirm that '" + TAName.getText() + "' only contains alphabetic characters.");
			alert.showAndWait();
		}
		if(!isNumeric(MaxHoursPerWeek.getText())) {
			alert.setContentText("Please confirm that '" + MaxHoursPerWeek.getText() + "' only contains numeric characters.");
			alert.showAndWait();
		}
		
		System.out.print("Is GA: ");
		System.out.println(isGA);
		//System.out.println(isGA.getSelectedToggle().getUserData().toString());
	
	}


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
