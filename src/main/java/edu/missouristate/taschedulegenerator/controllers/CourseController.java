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
	@FXML private CheckBox Monday;
	@FXML private CheckBox Tuesday;
	@FXML private CheckBox Wednesday;
	@FXML private CheckBox Thursday;
	@FXML private CheckBox Friday;
	@FXML private ComboBox<String> startSelection;
	@FXML private ComboBox<String> endSelection;
	@FXML private TableView<Activity> activityTable;
	@FXML
	private TableColumn<Activity, String> activityNameCol;

	public ObservableList<Activity> activities;

	@FXML
	public void cancel(ActionEvent event) {
		// This is an example of how to switch scenes without passing data
		SceneManager.showScene("dashboard");
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
		activities.add(newActivity);
		activityTable.getItems().add(newActivity);
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
		activityTable.setItems(activities);

	}
}
