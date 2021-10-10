package edu.missouristate.taschedulegenerator.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import edu.missouristate.taschedulegenerator.SceneManager;
import edu.missouristate.taschedulegenerator.SceneManager.Controller;
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

	@Override
	public void initData(String data) {
		// This is where you would process data before showing the view
		courseCode.setText(data);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}

}
