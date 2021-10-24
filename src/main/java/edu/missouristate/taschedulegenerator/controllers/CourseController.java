package edu.missouristate.taschedulegenerator.controllers;

import edu.missouristate.taschedulegenerator.domain.Activity;
import edu.missouristate.taschedulegenerator.domain.Course;
import edu.missouristate.taschedulegenerator.domain.TA;
import edu.missouristate.taschedulegenerator.domain.TimeBlock;
import edu.missouristate.taschedulegenerator.util.ActionCellFactory;
import edu.missouristate.taschedulegenerator.util.AppData;
import edu.missouristate.taschedulegenerator.util.SceneManager;
import edu.missouristate.taschedulegenerator.util.SceneManager.Controller;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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
		timeBlock.setDays(days);

		timeBlock.setStartTime(LocalTime.parse(startSelection.getValue(), AppData.TIME_FORMATTER));
		timeBlock.setEndTime(LocalTime.parse(endSelection.getValue(), AppData.TIME_FORMATTER));
		newActivity.setTime(timeBlock);
		activityTable.getItems().add(newActivity);
	}

	@FXML
	public void saveCourseInfo(ActionEvent event) {
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
}
