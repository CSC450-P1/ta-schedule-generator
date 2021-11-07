package edu.missouristate.taschedulegenerator.controllers;

import java.util.List;
import java.util.stream.Collectors;

import edu.missouristate.taschedulegenerator.domain.Schedule;
import edu.missouristate.taschedulegenerator.domain.Schedule.ScheduledActivity;
import edu.missouristate.taschedulegenerator.domain.Schedule.ScheduledTA;
import edu.missouristate.taschedulegenerator.util.AppData;
import edu.missouristate.taschedulegenerator.util.SceneManager;
import edu.missouristate.taschedulegenerator.util.SceneManager.Controller;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;

public class ScheduleController implements Controller<Void> {
	
	private List<Schedule> schedules;
	
	private int index = 0;
	
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
			this.schedules = schedules;
			this.index = 0;
			courseTable.setItems(FXCollections.observableArrayList(schedules.get(index).getScheduledActivities()));
			taTable.setItems(FXCollections.observableArrayList(schedules.get(index).getActivitiesByTA()));
			// TODO: Store schedules and populate schedule tables here
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
		});
	}
	
	@FXML
	public void nextSchedule(ActionEvent event) {
		if(!validateDisplay()) {
			return;
		}
		
		courseTable.setItems(FXCollections.observableArrayList(schedules.get(++index % schedules.size()).getScheduledActivities()));
		taTable.setItems(FXCollections.observableArrayList(schedules.get(index).getActivitiesByTA()));		
	}
	
	@FXML
	public void previousSchedule(ActionEvent event) {
		if(!validateDisplay()) {
			return;
		}
		
		courseTable.setItems(FXCollections.observableArrayList(schedules.get(--index + schedules.size() % schedules.size()).getScheduledActivities()));
		taTable.setItems(FXCollections.observableArrayList(schedules.get(index).getActivitiesByTA()));		
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
