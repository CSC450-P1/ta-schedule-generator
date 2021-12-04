package edu.missouristate.taschedulegenerator.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import edu.missouristate.taschedulegenerator.domain.Course;
import edu.missouristate.taschedulegenerator.domain.TA;
import edu.missouristate.taschedulegenerator.util.ActionCellFactory;
import edu.missouristate.taschedulegenerator.util.AppData;
import edu.missouristate.taschedulegenerator.util.SceneManager;
import edu.missouristate.taschedulegenerator.util.SceneManager.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class DashboardController implements Controller<Boolean> , Initializable {
	
	@FXML
	private TableView<TA> TAtable;
	
	@FXML 
	private TableView<Course> courseTable;
	
	@FXML
	public void addCourseInfo(ActionEvent event) {
		// This is an example of how to switch scenes and pass data to the new scene's controller to process before showing
		SceneManager.showScene("courseInfo", null);
	}
	
	@FXML
	public void addTAInfo(ActionEvent event) {
		SceneManager.showScene("taInfo", null);
	}
	
	@FXML
	public void generateSchedules(ActionEvent event) {
		if(AppData.getCourses().isEmpty() || AppData.getTAs().isEmpty()) {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Warning");
			alert.setHeaderText("Invalid Data Entry");
			alert.setContentText("There must be at least one course and one TA/GA to generate schedules.");
			alert.showAndWait();
			return;
		}
		SceneManager.showScene("schedules", null);
	}

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//Course Table
		courseTable.setPlaceholder(new Label("No courses have been added."));
		courseTable.setItems(AppData.getCourses());
		
		final TableColumn<Course, String> courseColumn = new TableColumn<>("Course");
		courseColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
		courseTable.getColumns().add(courseColumn);
		
		final TableColumn<Course, String> instructorColumn = new TableColumn<>("Instructor");
		instructorColumn.setCellValueFactory(new PropertyValueFactory<>("instructorName"));
		courseTable.getColumns().add(instructorColumn);
		
		final TableColumn<Course, Void> courseActionColumn = new TableColumn<>("Action");
		courseActionColumn.setCellFactory(new ActionCellFactory<>(
				(course) -> {
					SceneManager.showScene("courseInfo", course);
				},
				(course) -> {
					AppData.getCourses().remove(course);
				}));
		courseTable.getColumns().add(courseActionColumn);
	
		//TA table
		TAtable.setPlaceholder(new Label("No TAs have been added."));
		TAtable.setItems(AppData.getTAs());
		
		final TableColumn<TA, String> taColumn = new TableColumn<>("TA");
		taColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		TAtable.getColumns().add(taColumn);
		
		final TableColumn<TA, Void> TAactionCol = new TableColumn<TA, Void>("Action");
		TAactionCol.setCellFactory(new ActionCellFactory<>(
				(ta) -> { // edit
					SceneManager.showScene("taInfo", ta);
				},
				(ta) -> { // remove
					AppData.getTAs().remove(ta);
				}));
		TAtable.getColumns().add(TAactionCol);
		
	}

	@Override
	public void initData(Boolean refresh) {
		if(refresh) {
			TAtable.refresh();
			courseTable.refresh();
			}
	}

	@FXML
	public void clearInfo(ActionEvent event) {
		AppData.getCourses().clear();
		AppData.getTAs().clear();
	}
}
