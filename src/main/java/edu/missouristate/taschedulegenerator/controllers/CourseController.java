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

public class CourseController implements Controller<String>, Initializable {
	
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

	@FXML
	public void cancel(ActionEvent event) {
		try {
			saveCourseInfo(null);
			activityTable.getItems().clear();
		} catch (Exception e) {
			SceneManager.showScene("dashboard");
		}

	}

	@FXML
	public void addActivity(ActionEvent event) {
		if(!validateActivity()) {
			return;
		}
		
		Activity newActivity = new Activity();
		TimeBlock timeBlock = new TimeBlock();
		List<DayOfWeek> days = new ArrayList<DayOfWeek>();

		newActivity.setName(activityName.getText());
		newActivity.setHoursNeeded(Integer.parseInt(estimatedHours.getText()));

		newActivity.setMustBeTA(yesTA.isSelected());

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
		
		if(days.isEmpty() || StringUtils.isBlank(startSelection.getValue()) || StringUtils.isBlank(startSelection.getValue())) {
			newActivity.setTime(null);
		} else {
			timeBlock.setDays(days);
			timeBlock.setStartTime(LocalTime.parse(startSelection.getValue(), AppData.TIME_FORMATTER));
			timeBlock.setEndTime(LocalTime.parse(endSelection.getValue(), AppData.TIME_FORMATTER));
			newActivity.setTime(timeBlock);
		}
		
		activityTable.getItems().add(newActivity);
	}

	@FXML
	public void saveCourseInfo(ActionEvent event) {
		if(!validate()) {
			return;
		}
		Course newCourse = new Course();

		newCourse.setCourseCode(courseCode.getText());
		newCourse.setInstructorName(instructorName.getText());
		newCourse.setActivities(activityTable.getItems());


		AppData.getCourses().add(newCourse);
		SceneManager.showScene("dashboard", true);

		courseCode.setText(null);
		instructorName.setText(null);
	}

	@Override
	public void initData(String data) {
		// This is where you would process data before showing the view
		courseCode.setText(data);
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
		boolean valid = true;
		String errorMessage = null;
		if(StringUtils.isBlank(courseCode.getText())) {
			valid = false;
			errorMessage = "Please confirm that course code is not empty.";
		} else if(StringUtils.isBlank(instructorName.getText())) {
			valid = false;
			errorMessage = "Please confirm that instructor name is not empty.";
		} else if(activityTable.getItems().isEmpty()) {
			valid = false;
			errorMessage = "Please confirm that there is at least on activity.";
		}
		
		if(!valid) {
			showErrorMessage(errorMessage);
		}
		
		return valid;
	}
	
	private boolean validateActivity() {
		boolean valid = true;
		String errorMessage = null;
		
		if(StringUtils.isBlank(activityName.getText())) {
			valid = false;
			errorMessage = "Please confirm that activity name is not empty.";
		} else if(StringUtils.isBlank(estimatedHours.getText()) || !StringUtils.isNumeric(estimatedHours.getText())) {
			valid = false;
			errorMessage = "Please confirm that estimated hours is a number and is not empty.";
		} else if(!yesTA.isSelected() && !noTA.isSelected()) {
			valid = false;
			errorMessage = "Please select an option on if the activity needs a TA.";
		}
		
		if(!valid) {
			showErrorMessage(errorMessage);
		}
		
		return valid;
	}
	
	private void showErrorMessage(String message) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Warning");
		alert.setHeaderText("Invalid Data Entry");
		alert.setContentText(message);
		alert.showAndWait();
	}
}
