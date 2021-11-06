package edu.missouristate.taschedulegenerator.controllers;

import java.util.stream.Collectors;

import edu.missouristate.taschedulegenerator.domain.Schedule;
import edu.missouristate.taschedulegenerator.domain.Schedule.ScheduledActivity;
import edu.missouristate.taschedulegenerator.util.AppData;
import edu.missouristate.taschedulegenerator.util.SceneManager;
import edu.missouristate.taschedulegenerator.util.SceneManager.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class ScheduleController implements Controller<Void> {
	
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

}
