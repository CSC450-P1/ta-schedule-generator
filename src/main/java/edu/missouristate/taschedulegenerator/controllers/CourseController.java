package edu.missouristate.taschedulegenerator.controllers;

import edu.missouristate.taschedulegenerator.domain.Activity;
import edu.missouristate.taschedulegenerator.domain.TimeBlock;
import edu.missouristate.taschedulegenerator.util.SceneManager;
import edu.missouristate.taschedulegenerator.util.SceneManager.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

public class CourseController implements Controller<String> {
	
	@FXML
	private TextField courseCode;
	@FXML
	private TextField activityName;
	@FXML
	private TextField estimatedHours;
	@FXML
	private ToggleGroup mustBeTA;
	@FXML private CheckBox Monday;
	@FXML private CheckBox Tuesday;
	@FXML private CheckBox Wednesday;
	@FXML private CheckBox Thursday;
	@FXML private CheckBox Friday;
	
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

		RadioButton mustBeTAField = (RadioButton) mustBeTA.getSelectedToggle();
		if (mustBeTAField.getText() == "No"){
			newActivity.setMustBeTA(false);
		}else{
			newActivity.setMustBeTA(true);
		}

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
		newActivity.setTime(timeBlock);
	}

	@Override
	public void initData(String data) {
		// This is where you would process data before showing the view
		courseCode.setText(data);
	}

}
