package edu.missouristate.taschedulegenerator.controllers;

import edu.missouristate.taschedulegenerator.util.SceneManager;
import edu.missouristate.taschedulegenerator.util.SceneManager.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class CourseController implements Controller<String> {
	
	@FXML
	private TextField courseCode;
	
	@FXML
	public void cancel(ActionEvent event) {
		// This is an example of how to switch scenes without passing data
		SceneManager.showScene("dashboard");
	}

	@FXML
	public void addActivity(ActionEvent event) {
		// This is an example of how to switch scenes and pass data to the new scene's controller to process before showing
		SceneManager.showScene("activityInfo");
	}

	@Override
	public void initData(String data) {
		// This is where you would process data before showing the view
		courseCode.setText(data);
	}

}
