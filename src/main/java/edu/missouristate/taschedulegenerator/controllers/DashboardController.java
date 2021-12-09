package edu.missouristate.taschedulegenerator.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import edu.missouristate.taschedulegenerator.domain.Course;
import edu.missouristate.taschedulegenerator.domain.TA;
import edu.missouristate.taschedulegenerator.util.ActionCellFactory;
import edu.missouristate.taschedulegenerator.util.AppData;
import edu.missouristate.taschedulegenerator.util.GUIUtils;
import edu.missouristate.taschedulegenerator.util.SceneManager;
import edu.missouristate.taschedulegenerator.util.SceneManager.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Controller for the dashboard scene.
 * 
 * @author Noah Geren, Cody Sullins, Corey Rusher, Keegan Maynard
 *
 */
public class DashboardController implements Controller<Boolean> , Initializable {
	
	// All @FMXL fields are injected from the dashboard scene
	
	@FXML
	private TableView<TA> TAtable;
	
	@FXML 
	private TableView<Course> courseTable;
	
	/**
	 * Shows the courseInfo scene.
	 * 
	 * @param event The event that triggered this method.
	 * @see SceneManager
	 */
	@FXML
	public void addCourseInfo(ActionEvent event) {
		// This is an example of how to switch scenes and pass data to the new scene's controller to process before showing
		SceneManager.showScene("courseInfo", null);
	}
	
	/**
	 * Shows the taInfo scene.
	 * 
	 * @param event The event that triggered this method.
	 * @see SceneManager
	 */
	@FXML
	public void addTAInfo(ActionEvent event) {
		SceneManager.showScene("taInfo", null);
	}
	
	/**
	 * Shows the schedules scene.
	 * 
	 * @param event The event that triggered this method.
	 * @see SceneManager
	 */
	@FXML
	public void generateSchedules(ActionEvent event) {
		// Cannot generate schedules if there are no courses or TAs
		if(AppData.getCourses().isEmpty() || AppData.getTAs().isEmpty()) {
			GUIUtils.showError("Invalid Data Entry", "There must be at least one course and one TA/GA to generate schedules.");
			return;
		}
		SceneManager.showScene("schedules", null);
	}

	
	/**
	 * Setups up any fields or tables that are included in the scene.
	 */
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
				(course) -> { // Edit
					SceneManager.showScene("courseInfo", course);
				},
				(course) -> { // Remove
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

	/**
	 * Refreshes the course and TA tables if refresh is true.
	 * 
	 * @param refresh If the tables should be refreshed.
	 */
	@Override
	public void initData(Boolean refresh) {
		if(refresh) {
			TAtable.refresh();
			courseTable.refresh();
			}
	}

	/**
	 * Asks for confirmation before clearing the existing course and TA data.
	 * 
	 * @param event The event that triggered this method.
	 */
	@FXML
	public void clearInfo(ActionEvent event) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Clear Course and TA/GA Information");
		alert.setHeaderText("Are you sure?");
		alert.setContentText("Are you sure you want to clear all course and \nTA/GA information.");
		if(alert.showAndWait().get() == ButtonType.OK) {
			AppData.getCourses().clear();
			AppData.getTAs().clear();
		}
	}
}
