package edu.missouristate.taschedulegenerator.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * Controller for the errors scene.
 * 
 * @author Noah Geren
 *
 */
public class ErrorsController implements Initializable {
	
	/**
	 * Stores the error log messages.
	 */
	private static ObservableList<String> errors = FXCollections.observableArrayList();
	/**
	 * Stores the error value.
	 */
	private static SimpleIntegerProperty error = new SimpleIntegerProperty(0);
	
	// All @FMXL fields are injected from the errors scene
	
	@FXML
	private TableView<String> errorTable;
	
	@FXML
	private Label totalError;

	/**
	 * Setups up any fields or tables that are included in the scene.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		errorTable.setPlaceholder(new Label("No errors."));
		errorTable.setItems(errors);
		final TableColumn<String, String> errorColumn = new TableColumn<>("Error");
		errorColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue()));
		errorTable.getColumns().add(errorColumn);
		
		totalError.setText(String.valueOf(error.get()));
		error.addListener(new ChangeListener<>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				totalError.setText(newValue.toString());
			}
		});
	}
	
	/**
	 * Sets the displayed error value and error messages.
	 * 
	 * @param error The error value to be displayed.
	 * @param errors The error messages to be displayed.
	 */
	public static void setData(int error, List<String> errors) {
		ErrorsController.error.set(error);
		ErrorsController.errors.setAll(errors);
	}

}
