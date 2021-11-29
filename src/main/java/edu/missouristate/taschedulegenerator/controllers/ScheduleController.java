package edu.missouristate.taschedulegenerator.controllers;

import java.util.List;
import java.util.stream.Collectors;

import edu.missouristate.taschedulegenerator.domain.Schedule;
import edu.missouristate.taschedulegenerator.domain.Schedule.ScheduledActivity;
import edu.missouristate.taschedulegenerator.util.AppData;
import edu.missouristate.taschedulegenerator.util.SceneManager;
import edu.missouristate.taschedulegenerator.util.SceneManager.Controller;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;

public class ScheduleController implements Controller<Void> {
	
	private List<Schedule> schedules;
	
	private int index = 0;
	
	@FXML
	private Label scheduleNum;
	
	@FXML
	private TableView<ScheduledTA> taTable;
	
	@FXML
	private TableView<ScheduledActivity> courseTable;
	
	@FXML
	public void backToDashboard(ActionEvent event) {
		SceneManager.showScene("dashboard");
	}

	@Override
	public void initData(Void data) {
		// TODO: Show loading here
		System.out.println("Started Generating Schedules");
		final long startTime = System.currentTimeMillis();
		AppData.generateSchedules(schedules -> {
			if(schedules == null) {
				return;
			}
			this.schedules = schedules;
			this.index = 0;
			showSchedule();
			
			// The code below is just for testing the genetic algorithm
			System.out.println("Generated " + schedules.size() + " schedules in " + (System.currentTimeMillis() - startTime) + "ms" );
			System.out.println("Best Generated Schedule:");
			System.out.println(String.format("%s %10s %s %s", "Course", "Activity", "TA", "Hours"));
			final Schedule bestSchedule = schedules.get(0);
			System.out.println("Error Total: " + bestSchedule.getError());
			for(final ScheduledActivity activity : bestSchedule.getScheduledActivities()) {
				System.out.println(
						String.format(
								"%s %15s %s %dhrs",
								activity.getActivity().getCourse().getCourseCode(),
								activity.getActivity().getName(),
								activity.getTA().getName(),
								(activity.getHours())
								)
						);
			}
			System.out.println("Errors:");
			System.out.println(String.join("\n", bestSchedule.getErrorLog()));
			System.out.println("All Schedules Errors: " + schedules.stream().map(s -> String.valueOf(s.getError())).collect(Collectors.joining("\n")));
		},
		(ex) -> {
			// TODO: Show error message here
		});
	}
	
	@FXML
	public void nextSchedule(ActionEvent event) {		
		if(!validateDisplay()) {
			return;
		}
		index = (index + 1) % schedules.size();
		showSchedule();
	}
	
	@FXML
	public void previousSchedule(ActionEvent event) {
		if(!validateDisplay()) {
			return;
		}
		index = (index - 1 + schedules.size()) % schedules.size();
		showSchedule();
	}
	
	private void showSchedule() {
		courseTable.setItems(FXCollections.observableArrayList(schedules.get(index).getScheduledActivities()));
		taTable.setItems(FXCollections.observableArrayList(schedules.get(index).getActivitiesByTA()));
		scheduleNum.setText("Schedule " + (index + 1) + " of " + schedules.size());
	}
	
	private boolean validateDisplay() {
		String errorMessage = null;
		
		if (schedules == null || schedules.isEmpty())
			errorMessage = "No schedules available to be displayed.";
		
		if(errorMessage != null) {
			showErrorMessage(errorMessage);
		}
		
		return errorMessage == null;
	}
	
	private void showErrorMessage(String message) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Warning");
		alert.setHeaderText("Unexpected output");
		alert.setContentText(message);
		alert.showAndWait();
	}
}
