package edu.missouristate.taschedulegenerator.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class DashboardController {
	
	@FXML
	public void addCourseInfoClicked(ActionEvent event) {
		SceneController.INSTANCE.showScene("courseInfo");
	}

}
