package edu.missouristate.taschedulegenerator.controllers;

import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;

import edu.missouristate.taschedulegenerator.domain.Activity;
import edu.missouristate.taschedulegenerator.domain.Course;
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
import javafx.scene.control.cell.PropertyValueFactory;

public class CourseController implements Controller<Course>, Initializable {
	
	@FXML
	private TextField courseCode;
	@FXML
	private TextField activityName;
	@FXML
	private TextField estimatedHours;
	@FXML
	private RadioButton yesTA;
	@FXML
	private RadioButton noTA;
	@FXML
	private TextField instructorName;

	@FXML private CheckBox Monday;
	@FXML private CheckBox Tuesday;
	@FXML private CheckBox Wednesday;
	@FXML private CheckBox Thursday;
	@FXML private CheckBox Friday;
	@FXML private ComboBox<String> startSelection;
	@FXML private ComboBox<String> endSelection;
	@FXML private TableView<Activity> activityTable;
	
	private boolean isNew;
	private Course course;

	@FXML
	public void cancel(ActionEvent event) {
		SceneManager.showScene("dashboard");
	}

	@FXML
	public void addActivity(ActionEvent event) {
		if(!validateActivity()) {
			return;
		}
		
		final String name = activityName.getText();
		final int hours = Integer.parseInt(estimatedHours.getText());
		final boolean mustBeTA = yesTA.isSelected();
		
		final List<DayOfWeek> days = new ArrayList<DayOfWeek>();

		if(Monday.isSelected()){
			days.add(DayOfWeek.MONDAY);
		}
		if(Tuesday.isSelected()){
			days.add(DayOfWeek.TUESDAY);
		}
		if(Wednesday.isSelected()){
			days.add(DayOfWeek.WEDNESDAY);
		}
		if(Thursday.isSelected()){
			days.add(DayOfWeek.THURSDAY);
		}
		if(Friday.isSelected()){
			days.add(DayOfWeek.FRIDAY);
		}
		
		TimeBlock time = null;
		if(!days.isEmpty() && StringUtils.isNotBlank(startSelection.getValue()) && StringUtils.isNotBlank(startSelection.getValue())) {
			final LocalTime startTime = LocalTime.parse(startSelection.getValue(), AppData.TIME_FORMATTER);
			final LocalTime endTime = LocalTime.parse(endSelection.getValue(), AppData.TIME_FORMATTER);
			time = new TimeBlock(startTime, endTime, days);
		}
		
		course.getActivities().add(new Activity(name, mustBeTA, hours, time, course));
		
		clearActivityInputs(null);
	}

	@FXML
	public void saveCourseInfo(ActionEvent event) {
		if(!validate()) {
			return;
		}
		
		course.setCourseCode(courseCode.getText());
		course.setInstructorName(instructorName.getText());

		if(isNew) {
			AppData.getCourses().add(course);
		} else {
			AppData.save();
		}
		
		SceneManager.showScene("dashboard", true);
	}
	
	@FXML
	private void clearActivityInputs(ActionEvent event) {
		activityName.setText(null);
		estimatedHours.setText(null);
		noTA.setSelected(true);
		Monday.setSelected(false);
		Tuesday.setSelected(false);
		Wednesday.setSelected(false);
		Thursday.setSelected(false);
		Friday.setSelected(false);
		startSelection.setValue(null);
		endSelection.setValue(null);
	}

	@Override
	public void initData(Course course) {
		isNew = course == null;
		if(isNew) {
			course = new Course();
		}
		this.course = course;
		courseCode.setText(course.getCourseCode());
		instructorName.setText(course.getInstructorName());
		activityTable.setItems(course.getActivities());
		clearActivityInputs(null);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		startSelection.setItems(AppData.TIMES);
		endSelection.setItems(AppData.TIMES);

		final TableColumn<Activity, String> activityColumn = new TableColumn<>("Activity");
		activityColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		activityTable.getColumns().add(activityColumn);
	}
	
	private boolean validate() {
		String errorMessage = null;
		if(StringUtils.isBlank(courseCode.getText())) {
			errorMessage = "Please confirm that course code is not empty.";
		} else if(StringUtils.isBlank(instructorName.getText())) {
			errorMessage = "Please confirm that instructor name is not empty.";
		} else if(activityTable.getItems().isEmpty()) {
			errorMessage = "Please confirm that there is at least one activity.";
		}
		
		if(errorMessage != null) {
			showErrorMessage(errorMessage);
		}
		
		return errorMessage == null;
	}
	
	private boolean validateActivity() {
		String errorMessage = null;
		
		final boolean daySelected = Monday.isSelected() || Tuesday.isSelected() || Wednesday.isSelected() || Thursday.isSelected() || Friday.isSelected();
		if(StringUtils.isBlank(activityName.getText())) {
			errorMessage = "Please confirm that activity name is not empty.";
		} else if(StringUtils.isBlank(estimatedHours.getText()) || !StringUtils.isNumeric(estimatedHours.getText())) {
			errorMessage = "Please confirm that estimated hours is a number and is not empty.";
		} else if(!yesTA.isSelected() && !noTA.isSelected()) {
			errorMessage = "Please select an option on if the activity needs a TA.";
		} else if(daySelected && (StringUtils.isBlank(startSelection.getValue()) || StringUtils.isBlank(endSelection.getValue()))) {
			errorMessage = "Start and end times must be selected if the TA needs to be available on certain days.";
		} else if(!daySelected && (StringUtils.isNotBlank(startSelection.getValue()) || StringUtils.isNotBlank(endSelection.getValue()))) {
			errorMessage = "At least one day must be selected if the TA needs to be available at certain times.";
		} else if(daySelected) { // This validation should go last
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
	
	private void showErrorMessage(String message) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Warning");
		alert.setHeaderText("Invalid Data Entry");
		alert.setContentText(message);
		alert.showAndWait();
	}

}
