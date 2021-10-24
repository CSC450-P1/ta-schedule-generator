package edu.missouristate.taschedulegenerator.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import edu.missouristate.taschedulegenerator.domain.CoursesAndTAs;
import edu.missouristate.taschedulegenerator.domain.TA;
import edu.missouristate.taschedulegenerator.util.SceneManager;
import edu.missouristate.taschedulegenerator.util.SceneManager.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;



public class DashboardController implements Controller<TA> , Initializable {
	
	@FXML
	private TableView<TA> TAtable;
	
	@FXML
	private TableColumn<TA, String> nameCol, maxHoursCol;
	
	
	//private List<TA> listOfTAs = new ArrayList<TA>();
	
	@FXML
	public void addCourseInfo(ActionEvent event) {
		// This is an example of how to switch scenes and pass data to the new scene's controller to process before showing
		SceneManager.showScene("courseInfo");
	}
	
	@FXML
	public void addTAInfo(ActionEvent event) {
		SceneManager.showScene("taInfo");
	}

	@Override
	public void initData(TA data) {
		//listOfTAs.add(data);
		//System.out.println("The TA about to be added to dashboard/list is : " + data);
		//listOfTAs.add(data);
		
		TAtable.getItems().add(data);
		//listOfTAs.add(data);
		//System.out.println("The list after: " + listOfTAs);
		//System.out.println("After" + TAtable.getItems());
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
		maxHoursCol.setCellValueFactory(new PropertyValueFactory<>("maxHours"));
	
		TableColumn<TA, Void> TAactionCol = new TableColumn<TA, Void>("Action");
		TAactionCol.setCellFactory(col -> new TableCell<TA, Void>() {
			private final HBox buttonContainer;
			{
				Button editBtn = new Button("Edit");
				Button deleteBtn = new Button("Delete");
				
				editBtn.setOnAction(event -> {
					TA data = getTableView().getItems().get(getIndex());
					SceneManager.showScene("taInfo", data);
					getTableView().getItems().remove(getIndex());
				});
				
				deleteBtn.setOnAction(event -> {
					TA data = getTableView().getItems().get(getIndex());
					getTableView().getItems().remove(getIndex());
				});
				buttonContainer = new HBox(3, editBtn, deleteBtn);
			}
			
			@Override
		    public void updateItem(Void item, boolean empty) {
		        super.updateItem(item, empty);
		        setGraphic(empty ? null : buttonContainer);
		    }
		});

		TAtable.getColumns().add(TAactionCol);
		
	}

}
