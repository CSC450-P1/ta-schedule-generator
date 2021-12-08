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

/**
 * Controller for the courseInfo scene.
 * 
 * @author Noah Geren, Corey Rusher
 *
 */
public class CourseController implements Controller<Course>, Initializable {
	
	// All @FMXL fields are injected from the courseInfo scene
	
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
	
	/**
	 * Contains each day of the week checkbox.
	 */
	private List<CheckBox> daysOfWeek = null;
	
	/**
	 * Used for creating new course vs. editing a course
	 */
	private boolean isNew;
	/**
	 * The course being created or edited.
	 */
	private Course course;

	/**
	 * Cancels creating/editing a course. Returns back to dashboard scene.
	 * 
	 * @param event The event that triggered this method.
	 * @see SceneManager
	 */
	@FXML
	public void cancel(ActionEvent event) {
		SceneManager.showScene("dashboard");
	}

	/**
	 * Validates the current activity fields. If valid then it adds the activity to the course's activity list.
	 * 
	 * @param event The event that triggered this method.
	 */
	@FXML
	public void addActivity(ActionEvent event) {
		// Validate activity fields
		if(!validateActivity()) {
			return;
		}
		
		// Collect data for new activity
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
		
		// Add new activity
		course.getActivities().add(new Activity(name, mustBeTA, hours, time, course));
		
		// Clear activity fields
		clearActivityInputs();
	}

	/**
	 * Validates course info fields. If valid then either saves the the new course or updates the course being edited.
	 * 
	 * @param event The event that triggered this method.
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
	
	/**
	 * Initializes data for the courseInfo scene.
	 * 
	 * @param course The course that is being edited. Should be null if creating a new course.
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

	/**
	 * Setups up any fields or tables that are included in the scene.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		daysOfWeek = Arrays.asList(Monday, Tuesday, Wednesday, Thursday, Friday);
		// Populate start and end time options
		startSelection.setItems(AppData.TIMES);
		endSelection.setItems(AppData.TIMES);
		// Setup autocomplete for start and end times
		AutoCompleteComboBoxListener.addAutoComplete(startSelection);
        AutoCompleteComboBoxListener.addAutoComplete(endSelection);

        // Setup activity table columns
		final TableColumn<Activity, String> activityColumn = new TableColumn<>("Activity");
		activityColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		activityTable.getColumns().add(activityColumn);
		
		final TableColumn<Activity, Void> actionColumn = new TableColumn<>("Action");
		actionColumn.setCellFactory(new ActionCellFactory<>(
				(activity) -> { // Edit button clicked
					// Populate activity fields
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
				(activity) -> { // Remove button clicked
					activityTable.getItems().remove(activity);
				}));
		activityTable.getColumns().add(actionColumn);
	}
	
	/**
	 * Clears the activity input fields.
	 */
	private void clearActivityInputs() {
		activityName.setText(null);
		estimatedHours.setText(null);
		noTA.setSelected(true);
		daysOfWeek.forEach(day -> day.setSelected(false));
		startSelection.setValue(null);
		endSelection.setValue(null);
	}
	
	/**
	 * Validates the course info fields. Course code and instructor name cannot be empty. There also must be at least one activity.
	 * 
	 * @return True if the course info fields are valid.
	 */
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
	
	/**
	 * Validates the activity info fields.
	 * 
	 * @return True if the activity fields are valid.
	 */
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
