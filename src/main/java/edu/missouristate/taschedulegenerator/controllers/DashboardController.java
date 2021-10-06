package edu.missouristate.taschedulegenerator.controllers;

import edu.missouristate.taschedulegenerator.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class DashboardController {
	
	@FXML
	public void addCourseInfo(ActionEvent event) {
		// This is an example of how to switch scenes and pass data to the new scene's controller to process before showing
		SceneManager.showScene("courseInfo", "CSC450");
	}
	
	@FXML
	public void addTAInfo(ActionEvent event) {
		SceneManager.showScene("taInfo");
	}

}
