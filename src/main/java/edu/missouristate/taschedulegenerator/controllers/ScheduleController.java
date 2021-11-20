package edu.missouristate.taschedulegenerator.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.functions.Column;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import edu.missouristate.taschedulegenerator.domain.Course;
import edu.missouristate.taschedulegenerator.domain.Schedule;
import edu.missouristate.taschedulegenerator.domain.Schedule.ScheduledActivity;
import edu.missouristate.taschedulegenerator.domain.Schedule.ScheduledTA;
import edu.missouristate.taschedulegenerator.util.AppData;
import edu.missouristate.taschedulegenerator.util.SceneManager;
import edu.missouristate.taschedulegenerator.util.SceneManager.Controller;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

public class ScheduleController implements Controller<Void>, Initializable {

	@FXML
	private TableView<Schedule.ScheduledTA> taTable;

	@FXML
	private TableView<Schedule.ScheduledActivity> courseTable;
	
	private List<Schedule> schedules;
	
	private int index = 0;
	
	@FXML
	private Label scheduleNum;

	@FXML
	public void backToDashboard(ActionEvent event) {
		SceneManager.showScene("dashboard");
	}
	
	
	
	@FXML
	public void saveSchedule(ActionEvent event) throws IOException {
		String errorMessage = null;
		if (taTable.getItems().size() == 0 && courseTable.getItems().size() == 0) {
			errorMessage = "Please wait until schedules have been generated.";
			showErrorMessage(errorMessage);
			// Processing not done - prompt user with Alert
			// TODO: Create Alert message for processing not being complete
			//System.out.println("Processing not done please wait");
		} else if (taTable.getItems().size() > 0 && courseTable.getItems().size() > 0) {
			//System.out.println("The index is--------" + index);
			// Data has been processed so save it
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet spreadsheet = workbook.createSheet("TA Assignment");
		
			XSSFRow row = spreadsheet.createRow(0);
			
			for (int j = 0; j < taTable.getColumns().size(); j++) {
				row.createCell(j).setCellValue(taTable.getColumns().get(j).getText());
				spreadsheet.autoSizeColumn(j);
			}
			
			for (int i = 0; i < taTable.getItems().size(); i++) {
	            row = spreadsheet.createRow(i + 1);
	            spreadsheet.autoSizeColumn(i);
	            for (int j = 0; j < taTable.getColumns().size(); j++) {
	                if(taTable.getColumns().get(j).getCellData(i) != null) { 
	                    row.createCell(j).setCellValue(taTable.getColumns().get(j).getCellData(i).toString());
	                    spreadsheet.autoSizeColumn(j);
	                }
	                else {
	                    row.createCell(j).setCellValue("");
	                }   
	            }
	        }
			
			
			List<String> courses = new ArrayList<String>();
			for (int i = 0; i < courseTable.getItems().size(); i++) {
				String courseCode = courseTable.getItems().get(i).getActivity().getCourse().getCourseCode();
				String professor = courseTable.getItems().get(i).getActivity().getCourse().getInstructorName();
				
				
				if (!courses.contains(courseCode)) {
					courses.add(courseCode);
				} else if (courses.contains(courseCode)) {
					workbook.createSheet(courseCode + "-" + professor);
					courses.remove(courseCode);
				}
				
			}
			
			for (int i = 1; i < workbook.getNumberOfSheets(); i++) {
				XSSFSheet editSheet = workbook.getSheetAt(i);
				row = editSheet.createRow(0);
				
				for (int j = 1; j < courseTable.getColumns().size(); j++) {
					row.createCell(j).setCellValue(courseTable.getColumns().get(j).getText());
					editSheet.autoSizeColumn(j);
				}
				
				for (int t = 0; t < courseTable.getItems().size(); t++) {
		            row = editSheet.createRow(t + 1);
		            editSheet.autoSizeColumn(t);
		            for (int j = 0; j < courseTable.getColumns().size(); j++) {
		                if(courseTable.getColumns().get(j).getCellData(t) != null) { 
		                    row.createCell(j).setCellValue(courseTable.getColumns().get(j).getCellData(t).toString());
		                    //System.out.println(courseTable.getColumns().get(j).getCellData(t).toString());
		                    editSheet.autoSizeColumn(j);
		                }
		                else {
		                    row.createCell(j).setCellValue("");
		                }   
		            }
				}
			}
			/*
			// Cleanup Export due to writing all activities to all sheets
			for (int i = 1; i < workbook.getNumberOfSheets(); i++) {
				XSSFSheet sheet = workbook.getSheetAt(i);
				
				for (int r = sheet.getLastRowNum(); r >= 0; r--) {
					Row rowTest = sheet.getRow(r);
					if (rowTest != null) {
						for (int c = 0; c < rowTest.getLastCellNum(); c++) {
							Cell cell = rowTest.getCell(c);
							if(cell.toString().equals(StringUtils.substringBefore(sheet.getSheetName(), "-"))) {
								System.out.println("Class name and sheet name match!");
							}
						}
					}
				}
			}
			*/
			
			Window current = Stage.getWindows().stream().filter(Window::isShowing).findFirst().orElse(null);
			DirectoryChooser dChooser = new DirectoryChooser();
			dChooser.setTitle("Save Destination");
			File selectedDir = dChooser.showDialog(current);
			
			if (selectedDir == null) {
				// Nothing selected
			} else {
				File tempFile = new File(selectedDir.getAbsolutePath() + "/Generated TA Schedule.xlsx");
				if (tempFile.exists()) {
					Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to overwrite the existing file?", ButtonType.YES, ButtonType.NO);
					alert.setTitle("Duplicate File Already Exists");
					alert.showAndWait().ifPresent(response -> {
						if(response == ButtonType.YES) {
							FileOutputStream out;
							Alert errorAlert = new Alert(AlertType.ERROR);
							try {
								out = new FileOutputStream(tempFile.getAbsolutePath());
								workbook.write(out);
								out.close();
							} catch (FileNotFoundException e) {
								errorAlert.setHeaderText("Error saving schedule, please close any open schedules and try again.");
								errorAlert.setTitle("Error Occured During Saving");
								errorAlert.showAndWait();
							} catch (IOException e) {
								errorAlert.setHeaderText("Error savng schedule, please try again.");
								errorAlert.showAndWait();
								
							}
							
						} else if (response == ButtonType.NO) {
							// Do Nothing
						}
					});
					
				} else {
					// File does not exist so create the new file to save
					FileOutputStream out = new FileOutputStream(new File(selectedDir.getAbsolutePath() + "/Generated TA Schedule.xlsx"));
					workbook.write(out);
					out.close();
				}
				
				
				
			}
				/*
				for (Row rowt : sheet) {
					for (Cell cell : rowt) {
						String className = cell.toString();
						if (!className.equals(StringUtils.substringBefore(sheet.getSheetName(), "-"))) {
							//System.out.println("The class and sheet name match!");
							Row test = cell.getRow();
							
							sheet.removeRow(test);
						}
						//System.out.println(cell.toString());
					}
				}	
				*/
			}
			
		}
		
		
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		taTable.setEditable(true);
		//TA Table
		final TableColumn<Schedule.ScheduledTA, String> taColumn = new TableColumn<>("TA");
		taColumn.setCellFactory(TextFieldTableCell.<Schedule.ScheduledTA>forTableColumn());
		taColumn.setCellValueFactory(cell -> {
			return new SimpleStringProperty(cell.getValue().getTA().getName());
		});
		taTable.getColumns().add(taColumn);

		final TableColumn<Schedule.ScheduledTA, String> maxHoursColumn = new TableColumn<>("Max Hours");
		maxHoursColumn.setCellFactory(TextFieldTableCell.<Schedule.ScheduledTA>forTableColumn());
		maxHoursColumn.setCellValueFactory(cell -> {
			return new SimpleStringProperty(String.valueOf(cell.getValue().getTA().getMaxHours()));
		});
		taTable.getColumns().add(maxHoursColumn);

		final TableColumn<Schedule.ScheduledTA, String> assignedHoursColumn = new TableColumn<>("Assigned Hours");
		assignedHoursColumn.setCellFactory(TextFieldTableCell.<Schedule.ScheduledTA>forTableColumn());
		assignedHoursColumn.setCellValueFactory(cell -> {
			int assignedHours = 0;
			for(final ScheduledActivity activity : cell.getValue().getActivities()){
				assignedHours += activity.getHours();
			}
			return new SimpleStringProperty(String.valueOf(assignedHours));
		});
		taTable.getColumns().add(assignedHoursColumn);

		final TableColumn<Schedule.ScheduledTA, String> courseColumn = new TableColumn<>("Courses");
		courseColumn.setCellFactory(TextFieldTableCell.<Schedule.ScheduledTA>forTableColumn());
		courseColumn.setCellValueFactory(cell -> {
			return new SimpleStringProperty(
				cell.getValue().getActivities()
						.stream()
						.map(a -> String.format("%s %s", a.getActivity().getCourse().getCourseCode(), a.getActivity().getName()))
						.collect(Collectors.joining(", ")));
		});
		taTable.getColumns().add(courseColumn);

		//Course Table
		final TableColumn<Schedule.ScheduledActivity, String> courseTableColumn = new TableColumn<>("Course");
		courseTableColumn.setCellFactory(TextFieldTableCell.<ScheduledActivity>forTableColumn());
		courseTableColumn.setCellValueFactory(cell -> {
			return new SimpleStringProperty(cell.getValue().getActivity().getCourse().getCourseCode());
		});
		courseTable.getColumns().add(courseTableColumn);

		final TableColumn<Schedule.ScheduledActivity, String> activityColumn = new TableColumn<>("Activity");
		activityColumn.setCellFactory(TextFieldTableCell.<ScheduledActivity>forTableColumn());
		activityColumn.setCellValueFactory(cell -> {
			return new SimpleStringProperty(cell.getValue().getActivity().getName());
		});
		courseTable.getColumns().add(activityColumn);

		final TableColumn<Schedule.ScheduledActivity, String> hoursNeededColumn = new TableColumn<>("Hours Needed");
		hoursNeededColumn.setCellFactory(TextFieldTableCell.<ScheduledActivity>forTableColumn());
		hoursNeededColumn.setCellValueFactory(cell -> {
			return new SimpleStringProperty(String.valueOf(cell.getValue().getActivity().getHoursNeeded()));
		});
		courseTable.getColumns().add(hoursNeededColumn);

		final TableColumn<Schedule.ScheduledActivity, String> hoursAssignedColumn = new TableColumn<>("Hours Assigned");
		hoursAssignedColumn.setCellFactory(TextFieldTableCell.<ScheduledActivity>forTableColumn());
		hoursAssignedColumn.setCellValueFactory(cell -> {
			return new SimpleStringProperty(String.valueOf(cell.getValue().getHours()));
		});
		courseTable.getColumns().add(hoursAssignedColumn);

		final TableColumn<Schedule.ScheduledActivity, String> taTableColumn = new TableColumn<>("TA");
		taTableColumn.setCellFactory(TextFieldTableCell.<ScheduledActivity>forTableColumn());
		taTableColumn.setCellValueFactory(cell -> {
			return new SimpleStringProperty(cell.getValue().getTA().getName());
		});
		courseTable.getColumns().add(taTableColumn);

	}

	@Override
	public void initData(Void data) {
		// TODO: Show loading here
		System.out.println("Started Generating Schedules");
		final long startTime = System.currentTimeMillis();
		AppData.generateSchedules(schedules -> {
			this.schedules = schedules;
			this.index = 0;
			showSchedule();
			
			// The code below is just for testing the genetic algorithm
			System.out.println("Generated " + schedules.size() + " schedules in " + (System.currentTimeMillis() - startTime) + "ms" );
			System.out.println("Best Generated Schedule:");
			System.out.println(String.format("%s %10s %s %s", "Course", "Activity", "TA", "Hours"));
			final Schedule bestSchedule = schedules.get(0);

			List<Schedule.ScheduledTA> scheduledTAs = bestSchedule.getActivitiesByTA();
			taTable.setItems(FXCollections.observableArrayList(scheduledTAs));
			List<Schedule.ScheduledActivity> scheduledActivities = bestSchedule.getScheduledActivities();
			courseTable.setItems(FXCollections.observableArrayList(scheduledActivities));

			System.out.println("Error Total: " + bestSchedule.getError());
			for(final ScheduledActivity activity : bestSchedule.getScheduledActivities()) {
				System.out.println(
						String.format(
								"%s %15s %s %dhrs",
								activity.getActivity().getCourse().getCourseCode(),
								activity.getActivity().getName(),
								activity.getTA().getName(),
								(activity.getHours())
								)
						);
			}
			System.out.println("Errors:");
			System.out.println(String.join("\n", bestSchedule.getErrorLog()));
			System.out.println("All Schedules Errors: " + schedules.stream().map(s -> String.valueOf(s.getError())).collect(Collectors.joining("\n")));
		});
	}
	
	@FXML
	public void nextSchedule(ActionEvent event) {		
		if(!validateDisplay()) {
			return;
		}
		index = (index + 1) % schedules.size();
		showSchedule();
	}
	
	@FXML
	public void previousSchedule(ActionEvent event) {
		if(!validateDisplay()) {
			return;
		}
		index = (index - 1 + schedules.size()) % schedules.size();
		showSchedule();
	}
	
	private void showSchedule() {
		courseTable.setItems(FXCollections.observableArrayList(schedules.get(index).getScheduledActivities()));
		taTable.setItems(FXCollections.observableArrayList(schedules.get(index).getActivitiesByTA()));
		scheduleNum.setText("Schedule " + (index + 1) + " of " + schedules.size());
	}
	
	private boolean validateDisplay() {
		String errorMessage = null;
		
		if (schedules == null || schedules.isEmpty())
			errorMessage = "No schedules available to be displayed.";
		
		if(errorMessage != null) {
			showErrorMessage(errorMessage);
		}
		
		return errorMessage == null;
	}
	
	private void showErrorMessage(String message) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Warning");
		alert.setHeaderText("Processing Incomplete");
		alert.setContentText(message);
		alert.showAndWait();
	}
}
