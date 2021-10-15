package edu.missouristate.taschedulegenerator.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import edu.missouristate.taschedulegenerator.domain.TimeBlock;
import edu.missouristate.taschedulegenerator.util.SceneManager;
import edu.missouristate.taschedulegenerator.util.SceneManager.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

public class TAController implements Controller<String> {
	
	
	
	@FXML
	private TextField TAName, MaxHoursPerWeek;
	
	@FXML
	private RadioButton YesGAButton, NoGAButton;
	
	@FXML
	private ToggleGroup isGA;
	
	@FXML
	private TableView<TimeBlock> unavailableTable;
	
	@FXML
	private TableColumn<TimeBlock, String> dayUnavailable;
	
	
	
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
	
	@FXML
	public void addTAInfo(ActionEvent event) {
		SceneManager.showScene("taInfo");
	}
	
	@FXML
	public void addTimeUnavailable(ActionEvent event) {
		SceneManager.showScene("timeUnavailable");
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
		//checking if MaxHoursPerWeek is numeric as well as under or equal to 20
		if(!isNumeric(MaxHoursPerWeek.getText())) {
			alert.setContentText("Please confirm that '" + MaxHoursPerWeek.getText() + "' only contains numeric characters.");
			alert.showAndWait();
		}
		if(Integer.parseInt(MaxHoursPerWeek.getText()) > 20) {
			alert.setContentText("Please make sure the max hours are below or equal to 20.");
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
	@Override
	public void initData(String data) {
		// TODO Auto-generated method stub
		
	}
	
	
	@FXML
	public void cancel(ActionEvent event) {
		SceneManager.showScene("dashboard");
	}


	
	
}
