package edu.missouristate.taschedulegenerator.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import edu.missouristate.taschedulegenerator.domain.Course;
import edu.missouristate.taschedulegenerator.util.ActionCellFactory;
import edu.missouristate.taschedulegenerator.util.AppData;
import edu.missouristate.taschedulegenerator.util.SceneManager;
import edu.missouristate.taschedulegenerator.util.SceneManager.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class DashboardController implements Controller<Boolean>, Initializable {
	
	@FXML 
	private TableView<Course> courseTable;
	
	@FXML
	public void addCourseInfo(ActionEvent event) {
		// This is an example of how to switch scenes and pass data to the new scene's controller to process before showing
		SceneManager.showScene("courseInfo", "CSC450");
	}

	@FXML
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//Course Table
		courseTable.setPlaceholder(new Label("No courses have been added."));
		courseTable.setItems(AppData.getCourses());
		final TableColumn<Course, String> courseColumn = new TableColumn<>("Course");
		courseColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
		courseTable.getColumns().add(courseColumn);
		
		final TableColumn<Course, Void> courseActionColumn = new TableColumn<>("Action");
		courseActionColumn.setCellFactory(new ActionCellFactory<>(
				(course) -> {
					SceneManager.showScene("courseInfo", course);
				},
				(course) -> {
					AppData.getCourses().remove(course);
				}));
		courseTable.getColumns().add(courseActionColumn);
	}

	@Override
	public void initData(Boolean data) {
		// TODO Auto-generated method stub
		
	}

}
