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
	
	private static final List<String> TIMES = new ArrayList<>();
	private static final LocalTime END_TIME = LocalTime.of(20, 0);
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");

	static {
	    for(LocalTime time = LocalTime.of(8, 0); time.compareTo(END_TIME) <= 0; time=time.plusMinutes(15)) {
	        TIMES.add(time.format(TIME_FORMATTER));
	    }
	}
	private TA testTA = null;
	
	@FXML
	public void addTimeUnavailable(ActionEvent event) {
		//testTA = new TA();
		List<DayOfWeek> daysSelected = new ArrayList<DayOfWeek>();
		for(CheckBox day : daysOfWeek) {
			if(day.isSelected()) {
			  daysSelected.add(DayOfWeek.valueOf(day.getId().toUpperCase()));
			} 
		}
		LocalTime beginTime = LocalTime.parse(startSelection.getValue(), TIME_FORMATTER);
		LocalTime endTime = LocalTime.parse(endSelection.getValue(), TIME_FORMATTER);
	
		// Still need to add time validation 
		TimeBlock unavailable = new TimeBlock(beginTime, endTime, daysSelected);
		//blockUnavailable.add(unavailable);
		//testingDumb.setGA(Arrays.asList(unavailable) != null);
		unavailableTable.getItems().add(unavailable);
				//clearCurrentTimeBlockEntry(null);
	}
	
	
	@FXML
	public void cancel(ActionEvent event) {
		SceneManager.showScene("dashboard");
	}
	//TA testTA = null;
	@FXML
	public void saveTAInfo(ActionEvent event) {
		//testTA = new TA();
		TA toSave = new TA();
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
			toSave.setNotAvailable(unavailableTable.getItems());
			
			SceneManager.showScene("dashboard", toSave);
			clearCurrentTimeBlockEntry(null);
			//unavailableTable.getItems().clear();
			TAName.setText(null);
			MaxHoursPerWeek.setText(null);
			isGA.getSelectedToggle().setSelected(false);
		} 
		
		//System.out.println("Saving TA to Dashbaord" + testTA);
		
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
		//checking if MaxHoursPerWeek is numeric as well as under or equal to 20
		//!isNumeric(MaxHoursPerWeek.getText())
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
		startSelection.getItems().addAll(TIMES);
		endSelection.getItems().clear();
        endSelection.getItems().addAll(TIMES);
        
        unavailableTable.getItems().clear();
	}
	

	@Override
	public void initData(TA data) {
		if (data != null) {
			//TA is being passed to edit so populate fields
			System.out.println(data);
			TAName.setText(data.getName());
			MaxHoursPerWeek.setText(String.valueOf(data.getMaxHours()));
			if (data.isGA()) {
				YesGAButton.setSelected(true);
			} else {
				NoGAButton.setSelected(true);
			}
			//System.out.println("The passed TA has these times unavailable " + data.getNotAvailable());
			unavailableTable.getItems().addAll(data.getNotAvailable());

		} else {
			//testTA = new TA();
		}
		
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		daysOfWeek = Arrays.asList(Monday, Tuesday, Wednesday, Thursday, Friday);
		//Adding times to the time ComboBoxes
        startSelection.getItems().clear();
        startSelection.getItems().addAll(TIMES);
        endSelection.getItems().clear();
        endSelection.getItems().addAll(TIMES);
        
        
        startTimeCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        endTimeCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        daysCol.setCellValueFactory(new PropertyValueFactory<>("days"));
        
        
	}


	
	
}
