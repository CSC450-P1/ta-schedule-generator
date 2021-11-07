package edu.missouristate.taschedulegenerator.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import edu.missouristate.taschedulegenerator.domain.Course;
import edu.missouristate.taschedulegenerator.domain.Schedule;
import edu.missouristate.taschedulegenerator.domain.Schedule.ScheduledActivity;
import edu.missouristate.taschedulegenerator.domain.TA;
import edu.missouristate.taschedulegenerator.util.ActionCellFactory;
import edu.missouristate.taschedulegenerator.util.AppData;
import edu.missouristate.taschedulegenerator.util.SceneManager;
import edu.missouristate.taschedulegenerator.util.SceneManager.Controller;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

public class ScheduleController implements Controller<Void>, Initializable {

	@FXML
	private TableView<Schedule.ScheduledTA> taTable;

	@FXML
	private TableView<Schedule.ScheduledActivity> courseTable;

	@FXML
	public void backToDashboard(ActionEvent event) {
		SceneManager.showScene("dashboard");
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		taTable.setEditable(true);
		//TA Table
		final TableColumn<Schedule.ScheduledTA, String> taColumn = new TableColumn<>("TA");
		taColumn.setCellFactory(TextFieldTableCell.<Schedule.ScheduledTA>forTableColumn());
		taColumn.setCellValueFactory(cell -> {
			return new SimpleStringProperty(cell.getValue().getTA().getName());
		});
		taTable.getColumns().add(taColumn);

		final TableColumn<Schedule.ScheduledTA, String> maxHoursColumn = new TableColumn<>("Max Hours");
		maxHoursColumn.setCellFactory(TextFieldTableCell.<Schedule.ScheduledTA>forTableColumn());
		maxHoursColumn.setCellValueFactory(cell -> {
			return new SimpleStringProperty(String.valueOf(cell.getValue().getTA().getMaxHours()));
		});
		taTable.getColumns().add(maxHoursColumn);

		final TableColumn<Schedule.ScheduledTA, String> assignedHoursColumn = new TableColumn<>("Assigned Hours");
		assignedHoursColumn.setCellFactory(TextFieldTableCell.<Schedule.ScheduledTA>forTableColumn());
		assignedHoursColumn.setCellValueFactory(cell -> {
			int assignedHours = 0;
			for(final ScheduledActivity activity : cell.getValue().getActivities()){
				assignedHours += activity.getHours();
			}
			return new SimpleStringProperty(String.valueOf(assignedHours));
		});
		taTable.getColumns().add(assignedHoursColumn);

		final TableColumn<Schedule.ScheduledTA, String> courseColumn = new TableColumn<>("Courses");
		courseColumn.setCellFactory(TextFieldTableCell.<Schedule.ScheduledTA>forTableColumn());
		courseColumn.setCellValueFactory(cell -> {
			return new SimpleStringProperty(
				cell.getValue().getActivities()
						.stream()
						.map(a -> String.format("%s %s", a.getActivity().getCourse().getCourseCode(), a.getActivity().getName()))
						.collect(Collectors.joining(", ")));
		});
		taTable.getColumns().add(courseColumn);

		//Course Table
		final TableColumn<Schedule.ScheduledActivity, String> courseTableColumn = new TableColumn<>("Course");
		courseTableColumn.setCellFactory(TextFieldTableCell.<ScheduledActivity>forTableColumn());
		courseTableColumn.setCellValueFactory(cell -> {
			return new SimpleStringProperty(cell.getValue().getActivity().getCourse().getCourseCode());
		});
		courseTable.getColumns().add(courseTableColumn);

		final TableColumn<Schedule.ScheduledActivity, String> activityColumn = new TableColumn<>("Activity");
		activityColumn.setCellFactory(TextFieldTableCell.<ScheduledActivity>forTableColumn());
		activityColumn.setCellValueFactory(cell -> {
			return new SimpleStringProperty(cell.getValue().getActivity().getName());
		});
		courseTable.getColumns().add(activityColumn);

		final TableColumn<Schedule.ScheduledActivity, String> hoursNeededColumn = new TableColumn<>("Hours Needed");
		hoursNeededColumn.setCellFactory(TextFieldTableCell.<ScheduledActivity>forTableColumn());
		hoursNeededColumn.setCellValueFactory(cell -> {
			return new SimpleStringProperty(String.valueOf(cell.getValue().getActivity().getHoursNeeded()));
		});
		courseTable.getColumns().add(hoursNeededColumn);

		final TableColumn<Schedule.ScheduledActivity, String> hoursAssignedColumn = new TableColumn<>("Hours Assigned");
		hoursAssignedColumn.setCellFactory(TextFieldTableCell.<ScheduledActivity>forTableColumn());
		hoursAssignedColumn.setCellValueFactory(cell -> {
			return new SimpleStringProperty(String.valueOf(cell.getValue().getHours()));
		});
		courseTable.getColumns().add(hoursAssignedColumn);

		final TableColumn<Schedule.ScheduledActivity, String> taTableColumn = new TableColumn<>("TA");
		taTableColumn.setCellFactory(TextFieldTableCell.<ScheduledActivity>forTableColumn());
		taTableColumn.setCellValueFactory(cell -> {
			return new SimpleStringProperty(cell.getValue().getTA().getName());
		});
		courseTable.getColumns().add(taTableColumn);

	}

	@Override
	public void initData(Void data) {
		// TODO: Show loading here
		System.out.println("Started Generating Schedules");
		final long startTime = System.currentTimeMillis();
		AppData.generateSchedules(schedules -> {
			// TODO: Store schedules and populate schedule tables here
			// The code below is just for testing the genetic algorithm
			System.out.println("Generated " + schedules.size() + " schedules in " + (System.currentTimeMillis() - startTime) + "ms" );
			System.out.println("Best Generated Schedule:");
			System.out.println(String.format("%s %10s %s %s", "Course", "Activity", "TA", "Hours"));
			final Schedule bestSchedule = schedules.get(0);

			List<Schedule.ScheduledTA> scheduledTAs = bestSchedule.getActivitiesByTA();
			taTable.setItems(FXCollections.observableArrayList(scheduledTAs));
			List<Schedule.ScheduledActivity> scheduledActivities = bestSchedule.getScheduledActivities();
			courseTable.setItems(FXCollections.observableArrayList(scheduledActivities));

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

}
