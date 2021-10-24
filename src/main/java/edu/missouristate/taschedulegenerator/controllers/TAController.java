package edu.missouristate.taschedulegenerator.controllers;

import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;

import edu.missouristate.taschedulegenerator.domain.TA;
import edu.missouristate.taschedulegenerator.domain.TimeBlock;
import edu.missouristate.taschedulegenerator.util.AppData;
import edu.missouristate.taschedulegenerator.util.SceneManager;
import edu.missouristate.taschedulegenerator.util.SceneManager.Controller;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;

public class TAController implements Controller<TA>, Initializable {
	
	@FXML
	private TextField TAName, MaxHoursPerWeek;
	
	@FXML
	private ToggleGroup isGA;
	
	@FXML
	private RadioButton YesGAButton, NoGAButton;
	
	@FXML
	private CheckBox Monday, Tuesday, Wednesday, Thursday, Friday;
	
	@FXML
	private ComboBox<String> startSelection, endSelection;
	
	@FXML
	private TableView<TimeBlock> unavailableTable;
	
	@FXML
	private TableColumn<TimeBlock, String> timeUnavailableCol, startTimeCol, endTimeCol, daysCol, updateTimeOffCol;
	
	private List<CheckBox> daysOfWeek = null;
	
	
	private TA testTA = null;
	
	@FXML
	public void addTimeUnavailable(ActionEvent event) {
		List<DayOfWeek> daysSelected = new ArrayList<DayOfWeek>();
		for(CheckBox day : daysOfWeek) {
			if(day.isSelected()) {
			  daysSelected.add(DayOfWeek.valueOf(day.getId().toUpperCase()));
			} 
		}
		LocalTime beginTime = LocalTime.parse(startSelection.getValue(), AppData.TIME_FORMATTER);
		LocalTime endTime = LocalTime.parse(endSelection.getValue(), AppData.TIME_FORMATTER);
	
		// Still need to add time validation 
		TimeBlock unavailable = new TimeBlock(beginTime, endTime, daysSelected);
		unavailableTable.getItems().add(unavailable);
	}
	
	
	@FXML
	public void cancel(ActionEvent event) {
		try {
			saveTAInfo(null);
			unavailableTable.getItems().clear();
		} catch (Exception e) {
			SceneManager.showScene("dashboard");
		}
		
	}
	
	@FXML
	public void saveTAInfo(ActionEvent event) {
		TA toSave;
		if(validate()) {
			toSave = new TA();
			toSave.setName(TAName.getText());
			toSave.setMaxHours(Integer.parseInt(MaxHoursPerWeek.getText()));
			if(YesGAButton.isSelected()) {
				toSave.setGA(true);
			}
			if(NoGAButton.isSelected()) {
				toSave.setGA(false);
			}
			
			toSave.getNotAvailable().addAll(unavailableTable.getItems());
			
			AppData.getTAs().add(toSave);
			SceneManager.showScene("dashboard", true);
			clearCurrentTimeBlockEntry(null);
			
			TAName.setText(null);
			MaxHoursPerWeek.setText(null);
			isGA.getSelectedToggle().setSelected(false);
		} 
		
	
		
	}

	private boolean validate() {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Warning");
		alert.setHeaderText("Invalid Data Entry");
		Boolean dataValidated = true;
		if(!StringUtils.isAlphaSpace(TAName.getText()) | TAName.getText().isEmpty()) {
			alert.setContentText("Please confirm that the 'TA Name' only contains alphabetic characters and is not empty.");
			alert.showAndWait();
			dataValidated = false;
		} 
		if(!StringUtils.isNumeric(MaxHoursPerWeek.getText())){
			alert.setContentText("Please confirm that 'Max Hours' only contains numeric characters and is not empty.");
			alert.showAndWait();
			dataValidated = false;
		}
		String maxHours = MaxHoursPerWeek.getText();
		if(StringUtils.isNumeric(maxHours) && Double.parseDouble(maxHours) > 20) {
			alert.setContentText("Please confirm that 'Max Hours' is less than 20.");
			alert.showAndWait();
			dataValidated = false;
			
		}
		
		if(!YesGAButton.isSelected() && !NoGAButton.isSelected()) {
			alert.setContentText("Please select an option whether the TA is also a GA.");
			alert.showAndWait();
			dataValidated = false;
		}
		return dataValidated;
		
	}
	
	@FXML
	public void clearCurrentTimeBlockEntry(ActionEvent event) {
		//Restting Day CheckBoxes
		for(CheckBox day : daysOfWeek) {
			if(day.isSelected()) {
				day.setSelected(false);
			} 
		}
		//Resetting Time Selections
		startSelection.getItems().clear();
		startSelection.getItems().addAll(AppData.TIMES);
		endSelection.getItems().clear();
        endSelection.getItems().addAll(AppData.TIMES);
        
        unavailableTable.getItems().clear();
	}
	

	@Override
	public void initData(TA data) {
		if (data != null) {
			TAName.setText(data.getName());
			MaxHoursPerWeek.setText(String.valueOf(data.getMaxHours()));
			if (data.isGA()) {
				YesGAButton.setSelected(true);
			} else {
				NoGAButton.setSelected(true);
			}
			unavailableTable.setItems(FXCollections.observableList(data.getNotAvailable()));
		} 
		
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		daysOfWeek = Arrays.asList(Monday, Tuesday, Wednesday, Thursday, Friday);
		//Adding times to the time ComboBoxes
        startSelection.getItems().clear();
        startSelection.getItems().addAll(AppData.TIMES);
        endSelection.getItems().clear();
        endSelection.getItems().addAll(AppData.TIMES);
        
        
        startTimeCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTimeCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        daysCol.setCellValueFactory(new PropertyValueFactory<>("days"));
        
        
	}


	
	
}
