package edu.missouristate.taschedulegenerator.controllers;

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
		AppData.generateSchedules(schedules -> {
			// TODO: Populate tables here
		});
	}

}
