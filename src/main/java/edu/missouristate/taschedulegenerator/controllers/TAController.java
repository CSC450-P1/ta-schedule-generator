/*
Rev Name   Date      Description

*/

package edu.missouristate.taschedulegenerator.controllers;

import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import edu.missouristate.taschedulegenerator.domain.TA;
import edu.missouristate.taschedulegenerator.domain.TimeBlock;
import edu.missouristate.taschedulegenerator.util.ActionCellFactory;
import edu.missouristate.taschedulegenerator.util.AppData;
import edu.missouristate.taschedulegenerator.util.AutoCompleteComboBoxListener;
import edu.missouristate.taschedulegenerator.util.GUIUtils;
import edu.missouristate.taschedulegenerator.util.SceneManager;
import edu.missouristate.taschedulegenerator.util.SceneManager.Controller;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

/** JavaDoc comment for public class TAController
*/
public class TAController implements Controller<TA>, Initializable {
	
	private static final String[] DAY_ABBREVIATIONS = {"M", "T", "W", "Th", "F"};
	
	@FXML
	private TextField TAName, MaxHoursPerWeek;
	
	@FXML
	private ToggleGroup isGA;
	
	@FXML
	private RadioButton TAButton, GAButton;
	
	@FXML
	private CheckBox Monday, Tuesday, Wednesday, Thursday, Friday;
	
	@FXML
	private ComboBox<String> startSelection, endSelection;
	
	@FXML
	private TableView<TimeBlock> unavailableTable;
	
	private List<CheckBox> daysOfWeek = null;
	
	private boolean isNew;
	private TA ta;
	
	/** JavaDoc comment for public method addTimeUnavailable
	*/
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

		clearCurrentTimeBlockEntry();
	}
	
	/** JavaDoc comment for public method cancel
	*/
	@FXML
	public void cancel(ActionEvent event) {
		SceneManager.showScene("dashboard");
	}
	
	/** JavaDoc comment for public method saveTAInfo
	*/
	@FXML
	public void saveTAInfo(ActionEvent event) {
		if(!validate()) {
			return;
		}
		ta.setName(TAName.getText());
		ta.setMaxHours(Integer.parseInt(MaxHoursPerWeek.getText()));
		ta.setTA(TAButton.isSelected());
		
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
		if(StringUtils.isBlank(TAName.getText())) {
			errorMessage = "Please confirm that the 'TA Name' is not empty.";
		} else if(!StringUtils.isNumeric(maxHours)){
			errorMessage = "Please confirm that 'Max Hours' only contains numeric characters and is not empty.";
		} else if(StringUtils.isNumeric(maxHours) && Integer.parseInt(maxHours) > 20) {
			errorMessage = "Please confirm that 'Max Hours' is less than 20.";
		} else if(!TAButton.isSelected() && !GAButton.isSelected()) {
			errorMessage = "Please select if the GA is also a TA.";
		}
		
		if(errorMessage != null) {
			GUIUtils.showError("Invalid Data Entry", errorMessage);
		}
		
		return errorMessage == null;
	}
	
	private boolean validateTimeUnavailable() {
		String errorMessage = null;
		
		if(daysOfWeek.stream().noneMatch(day -> day.isSelected())) {
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
			GUIUtils.showError("Invalid Data Entry", errorMessage);
		}
		
		return errorMessage == null;
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
		if(ta.isTA()) {
			TAButton.setSelected(true);
		} else {
			GAButton.setSelected(true);
		}
		unavailableTable.setItems(ta.getNotAvailable());

		clearCurrentTimeBlockEntry();
	}

	/** JavaDoc comment for public method initialize
	*/
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		daysOfWeek = Arrays.asList(Monday, Tuesday, Wednesday, Thursday, Friday);
		//Adding times to the time ComboBoxes
        startSelection.setItems(AppData.TIMES);
        endSelection.setItems(AppData.TIMES);
        
        AutoCompleteComboBoxListener.addAutoComplete(startSelection);
        AutoCompleteComboBoxListener.addAutoComplete(endSelection);
        
        final TableColumn<TimeBlock, String> timeColumn = new TableColumn<>("Time");
        timeColumn.setCellValueFactory(cell -> {
        	final TimeBlock time = cell.getValue();
        	return new SimpleStringProperty(String.format("%s %s - %s", 
        			
        			time.getDays().stream().map(day -> DAY_ABBREVIATIONS[day.getValue() - 1]).collect(Collectors.joining()),
        			AppData.TIME_FORMATTER.format(time.getStartTime()),
        			AppData.TIME_FORMATTER.format(time.getEndTime())
        		));
        });
		unavailableTable.getColumns().add(timeColumn);

        final TableColumn<TimeBlock, Void> actionColumn = new TableColumn<>("Action");
		actionColumn.setCellFactory(new ActionCellFactory<>(
				(time) -> {
					daysOfWeek.forEach(day -> day.setSelected(time.getDays().contains(DayOfWeek.valueOf(day.getId().toUpperCase()))));
					startSelection.setValue(AppData.TIME_FORMATTER.format(time.getStartTime()));
					endSelection.setValue(AppData.TIME_FORMATTER.format(time.getEndTime()));
					unavailableTable.getItems().remove(time);
				},
				(time) -> {
					unavailableTable.getItems().remove(time);
				}));
		unavailableTable.getColumns().add(actionColumn);
	}
	
	public void clearCurrentTimeBlockEntry() {
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
}
