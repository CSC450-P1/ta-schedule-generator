package edu.missouristate.taschedulegenerator.controllers;

import edu.missouristate.taschedulegenerator.util.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class ScheduleController {
	
	@FXML
	public void backToDashboard(ActionEvent event) {
		SceneManager.showScene("dashboard");
	}

}
