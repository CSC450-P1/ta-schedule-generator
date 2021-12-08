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

import org.apache.commons.lang3.StringUtils;

import edu.missouristate.taschedulegenerator.domain.Activity;
import edu.missouristate.taschedulegenerator.domain.Course;
import edu.missouristate.taschedulegenerator.domain.TimeBlock;
import edu.missouristate.taschedulegenerator.util.ActionCellFactory;
import edu.missouristate.taschedulegenerator.util.AppData;
import edu.missouristate.taschedulegenerator.util.AutoCompleteComboBoxListener;
import edu.missouristate.taschedulegenerator.util.GUIUtils;
import edu.missouristate.taschedulegenerator.util.SceneManager;
import edu.missouristate.taschedulegenerator.util.SceneManager.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

/**JavaDoc comment for public class CourseController
*
*/

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
	
	private List<CheckBox> daysOfWeek = null;
	
	private boolean isNew;
	private Course course;

	/** JavaDoc comment for public method cancel
	*/
	@FXML
	public void cancel(ActionEvent event) {
		SceneManager.showScene("dashboard");
	}

	/** JavaDoc comment for public method addActivity
	*/
	@FXML
	public void addActivity(ActionEvent event) {
		if(!validateActivity()) {
			return;
		}
		
		final String name = activityName.getText();
		final int hours = Integer.parseInt(estimatedHours.getText());
		final boolean mustBeTA = yesTA.isSelected();
		
		List<DayOfWeek> daysSelected = new ArrayList<DayOfWeek>();
		for(CheckBox day : daysOfWeek) {
			if(day.isSelected()) {
			  daysSelected.add(DayOfWeek.valueOf(day.getId().toUpperCase()));
			} 
		}
		
		TimeBlock time = null;
		if(!daysSelected.isEmpty() && StringUtils.isNotBlank(startSelection.getValue()) && StringUtils.isNotBlank(startSelection.getValue())) {
			final LocalTime startTime = LocalTime.parse(startSelection.getValue(), AppData.TIME_FORMATTER);
			final LocalTime endTime = LocalTime.parse(endSelection.getValue(), AppData.TIME_FORMATTER);
			time = new TimeBlock(startTime, endTime, daysSelected);
		}
		
		course.getActivities().add(new Activity(name, mustBeTA, hours, time, course));
		
		clearActivityInputs();
	}

	/** JavaDoc comment for public method saveCourseInfo
	*/
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
	
	

	/** JavaDoc comment for public method initData
	*/
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
		clearActivityInputs();
	}

	/** JavaDoc comment for public method initialize
	*/
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		daysOfWeek = Arrays.asList(Monday, Tuesday, Wednesday, Thursday, Friday);
		startSelection.setItems(AppData.TIMES);
		endSelection.setItems(AppData.TIMES);
		
		AutoCompleteComboBoxListener.addAutoComplete(startSelection);
        AutoCompleteComboBoxListener.addAutoComplete(endSelection);

		final TableColumn<Activity, String> activityColumn = new TableColumn<>("Activity");
		activityColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		activityTable.getColumns().add(activityColumn);
		
		final TableColumn<Activity, Void> actionColumn = new TableColumn<>("Action");
		actionColumn.setCellFactory(new ActionCellFactory<>(
				(activity) -> {
					activityName.setText(activity.getName());
					estimatedHours.setText(String.valueOf(activity.getHoursNeeded()));
					if(activity.isMustBeTA()) {
						yesTA.setSelected(true);
					} else {
						noTA.setSelected(true);
					}
					if(activity.getTime() != null) {
						daysOfWeek.forEach(day -> day.setSelected(activity.getTime().getDays().contains(DayOfWeek.valueOf(day.getId().toUpperCase()))));
						startSelection.setValue(AppData.TIME_FORMATTER.format(activity.getTime().getStartTime()));
						endSelection.setValue(AppData.TIME_FORMATTER.format(activity.getTime().getEndTime()));
					}
					activityTable.getItems().remove(activity);
				},
				(activity) -> {
					activityTable.getItems().remove(activity);
				}));
		activityTable.getColumns().add(actionColumn);
	}
	
	private void clearActivityInputs() {
		activityName.setText(null);
		estimatedHours.setText(null);
		noTA.setSelected(true);
		daysOfWeek.forEach(day -> day.setSelected(false));
		startSelection.setValue(null);
		endSelection.setValue(null);
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
			GUIUtils.showError("Invalid Data Entry", errorMessage);
		}
		
		return errorMessage == null;
	}
	
	private boolean validateActivity() {
		String errorMessage = null;
		
		final boolean daySelected = daysOfWeek.stream().anyMatch(day -> day.isSelected());
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
			GUIUtils.showError("Invalid Data Entry", errorMessage);
		}
		
		return errorMessage == null;
	}

}
