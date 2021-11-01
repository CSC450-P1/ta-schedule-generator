package edu.missouristate.taschedulegenerator.controllers;

import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalTime;
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
	
	private boolean isNew;
	private TA ta;
	
	@FXML
	public void addTimeUnavailable(ActionEvent event) {
		if(!validateTimeUnavailable()) {
			return;
		}
		List<DayOfWeek> daysSelected = new ArrayList<DayOfWeek>();
		for(CheckBox day : daysOfWeek) {
			if(day.isSelected()) {
			  daysSelected.add(DayOfWeek.valueOf(day.getId().toUpperCase()));
			} 
		}
		LocalTime beginTime = LocalTime.parse(startSelection.getValue(), AppData.TIME_FORMATTER);
		LocalTime endTime = LocalTime.parse(endSelection.getValue(), AppData.TIME_FORMATTER);

		ta.getNotAvailable().add(new TimeBlock(beginTime, endTime, daysSelected));

		clearCurrentTimeBlockEntry(null);
	}
	
	
	@FXML
	public void cancel(ActionEvent event) {
		SceneManager.showScene("dashboard");
	}
	
	@FXML
	public void saveTAInfo(ActionEvent event) {
		if(!validate()) {
			return;
		}
		ta.setName(TAName.getText());
		ta.setMaxHours(Integer.parseInt(MaxHoursPerWeek.getText()));
		ta.setGA(YesGAButton.isSelected());
		
		if(isNew) {
			AppData.getTAs().add(ta);
		} else {
			AppData.save();
		}
			
		SceneManager.showScene("dashboard", true);
	}

	private boolean validate() {
		String errorMessage = null;
		
		final String maxHours = MaxHoursPerWeek.getText();
		if(!StringUtils.isAlphaSpace(TAName.getText()) || StringUtils.isBlank(TAName.getText())) {
			errorMessage = "Please confirm that the 'TA Name' only contains alphabetic characters and is not empty.";
		} else if(!StringUtils.isNumeric(maxHours)){
			errorMessage = "Please confirm that 'Max Hours' only contains numeric characters and is not empty.";
		} else if(StringUtils.isNumeric(maxHours) && Integer.parseInt(maxHours) > 20) {
			errorMessage = "Please confirm that 'Max Hours' is less than 20.";
		} else if(!YesGAButton.isSelected() && !NoGAButton.isSelected()) {
			errorMessage = "Please select an option whether the TA is also a GA.";
		}
		
		if(errorMessage != null) {
			showErrorMessage(errorMessage);
		}
		
		return errorMessage == null;
	}
	
	private boolean validateTimeUnavailable() {
		String errorMessage = null;
		
		if(!(Monday.isSelected() || Tuesday.isSelected() || Wednesday.isSelected() || Thursday.isSelected() || Friday.isSelected())) {
			errorMessage = "Please select at least one day of the week.";
		} else if(StringUtils.isBlank(startSelection.getValue())) {
			errorMessage = "Please make sure to select a start time.";
		} else if(StringUtils.isBlank(endSelection.getValue())) {
			errorMessage = "Please make sure to select an end time";
		} else { // This validation should go last
			final LocalTime startTime = LocalTime.parse(startSelection.getValue(), AppData.TIME_FORMATTER);
			final LocalTime endTime = LocalTime.parse(endSelection.getValue(), AppData.TIME_FORMATTER);
			if(startTime.compareTo(endTime) >= 0) {
				errorMessage = "The end time must be after the start time";
			}
		}
		
		if(errorMessage != null) {
			showErrorMessage(errorMessage);
		}
		
		return errorMessage == null;
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
		startSelection.setValue(null);
		endSelection.setValue(null);
	}
	

	@Override
	public void initData(TA ta) {
		isNew = ta == null;
		if(isNew) {
			ta = new TA();
		}
		this.ta = ta;
		TAName.setText(ta.getName());
		MaxHoursPerWeek.setText(String.valueOf(ta.getMaxHours()));
		if(ta.isGA()) {
			YesGAButton.setSelected(true);
		} else {
			NoGAButton.setSelected(true);
		}
		unavailableTable.setItems(ta.getNotAvailable());

		clearCurrentTimeBlockEntry(null);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		daysOfWeek = Arrays.asList(Monday, Tuesday, Wednesday, Thursday, Friday);
		//Adding times to the time ComboBoxes
        startSelection.setItems(AppData.TIMES);
        endSelection.setItems(AppData.TIMES);
        
        
        startTimeCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTimeCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        daysCol.setCellValueFactory(new PropertyValueFactory<>("days"));
	}

	private void showErrorMessage(String message) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Warning");
		alert.setHeaderText("Invalid Data Entry");
		alert.setContentText(message);
		alert.showAndWait();
	}

}
