/*
 * MIT License
 * 
 * Copyright (c) 2021 Missouri State University
 * 
 * See LICENSE in the project's root directory for full license details
 * 
 */
package edu.missouristate.taschedulegenerator.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import edu.missouristate.taschedulegenerator.domain.Schedule;
import edu.missouristate.taschedulegenerator.domain.Schedule.ScheduledActivity;
import edu.missouristate.taschedulegenerator.domain.Schedule.ScheduledTA;
import edu.missouristate.taschedulegenerator.util.AppData;
import edu.missouristate.taschedulegenerator.util.GUIUtils;
import edu.missouristate.taschedulegenerator.util.SceneManager;
import edu.missouristate.taschedulegenerator.util.SceneManager.Controller;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Controller for the schedule scene.
 * 
 * SDD 3.2.1
 * 
 * @author Noah Geren, Cody Sullins, Corey Rusher, Carlos Izaguirre
 *
 */
public class ScheduleController implements Controller<Void>, Initializable{
	
	/**
	 * The schedules that were generated.
	 */
	private List<Schedule> schedules;
	/**
	 * The current index showing of the generated schedules.
	 */
	private int index = 0;
	/**
	 * The Future that is generating schedules. Used to cancel generating schedules.
	 */
	private Future<?> generator = null;
	
	// All @FMXL fields are injected from the schedules scene
	
	@FXML
	private Pane loadingPane;
	
	@FXML
	private Label scheduleNum;
	
	@FXML
	private TableView<ScheduledTA> taTable;
	
	@FXML
	private TableView<ScheduledActivity> courseTable;
	
	/**
	 * Empties the data in the errors window and shows the dashboard scene.
	 * 
	 * @param event The event that triggered this method.
	 * @see SceneManager
	 */
	@FXML
	public void backToDashboard(ActionEvent event) {
		ErrorsController.setData(0, Collections.emptyList());
		SceneManager.showScene("dashboard");
	}
	
	/**
	 * Generates an Excel file based on the currently viewed schedule. Then shows a file chooser to select where to save the Excel file.
	 * 
	 * @param event The event that triggered this method.
	 * @throws IOException If there is an unhandled exception when saving the schedule.
	 */
	@FXML
    public void saveSchedule(ActionEvent event) throws IOException {
        if (taTable.getItems().size() > 0 && courseTable.getItems().size() > 0) {

            // Data has been processed 
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet spreadsheet = workbook.createSheet("TA Assignment");

            XSSFRow row = spreadsheet.createRow(0);

            // Setting the TA Table Column values
            for (int j = 0; j < taTable.getColumns().size(); j++) {
                row.createCell(j).setCellValue(taTable.getColumns().get(j).getText());
            }

            // Populate first sheet
            int l = 1;
            for (ScheduledTA ta: taTable.getItems()) {
                row = spreadsheet.createRow(l);
                System.out.println(ta.toString());

                row.createCell(0).setCellValue(ta.getTA().getName());
                row.createCell(1).setCellValue(ta.getTA().getMaxHours());
                int assignedHours = 0;
                String assignedActivity = "";
                for (ScheduledActivity activity: ta.getActivities()) {
                    assignedHours = assignedHours + activity.getHours();
                    assignedActivity = assignedActivity + activity.getActivity().getCourse().getCourseCode() + "-" + activity.getActivity().getName() + ", ";
                }
                row.createCell(2).setCellValue(assignedHours);
                row.createCell(3).setCellValue(StringUtils.chop(StringUtils.chop(assignedActivity)));
                l++;
            }


            // Start collecting data for each course
            List <String> courses = new ArrayList <String> ();

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

            // Populate each course sheet
            for (int i = 1; i < workbook.getNumberOfSheets(); i++) {
                XSSFSheet editSheet = workbook.getSheetAt(i);
                row = editSheet.createRow(0);
                //Setting the Column Title values
                for (int j = 0; j < courseTable.getColumns().size(); j++) {
                    row.createCell(j).setCellValue(courseTable.getColumns().get(j).getText());
                }
                int k = 1;
                for (ScheduledActivity item: courseTable.getItems()) {
                    row = editSheet.createRow(k);

                    String sheetName = StringUtils.substringBefore(editSheet.getSheetName(), "-" + item.getActivity().getCourse().getInstructorName());
                    if (sheetName.equals(item.getActivity().getCourse().getCourseCode())) {
                        row.createCell(0).setCellValue(item.getActivity().getCourse().getCourseCode());
                        row.createCell(1).setCellValue(item.getActivity().getName());
                        row.createCell(2).setCellValue(item.getActivity().getHoursNeeded());
                        row.createCell(3).setCellValue(item.getHours());
                        row.createCell(4).setCellValue(item.getTA().getName());
                        k++;
                    }

                }
            }
            // Resize columns on each sheet
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                XSSFSheet sheet = workbook.getSheetAt(i);
                if (sheet.getPhysicalNumberOfRows() > 0) {
                    XSSFRow rowT = sheet.getRow(sheet.getFirstRowNum());
                    Iterator<Cell> cellIterator = rowT.cellIterator();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        int columnIndex = cell.getColumnIndex();
                        sheet.autoSizeColumn(columnIndex);
                    }
                }
            }
            // Show file chooser dialog to select where to save
            Window current = Stage.getWindows().stream().filter(Window::isShowing).findFirst().orElse(null);
            FileChooser fChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel Workbook", "*.xlsx");
            fChooser.getExtensionFilters().add(extFilter);
            fChooser.setInitialFileName("Generated TA Schedule " + (index + 1));
            File tempFile = fChooser.showSaveDialog(current);
            // Save excel file
            if (tempFile != null) {
                try (FileOutputStream out = new FileOutputStream(tempFile.getAbsolutePath())) {
                    workbook.write(out);
                } catch (IOException e) {
                	GUIUtils.showError("Error Occured During Saving", 
                			"Error saving schedule, please close any open schedules and try again.");
                }
            }
            workbook.close();
        }

    }


	/**
	 * Setups up any fields or tables that are included in the scene.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		taTable.setEditable(true);
		//TA Table
		final TableColumn<Schedule.ScheduledTA, String> taColumn = new TableColumn<>("TA/GA");
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
	
	/**
	 * Shows loading message, starts generating schedules, and setup callbacks for when generating is complete or an exception occurs.
	 */
	@Override
	public void initData(Void data) {
		loadingPane.setVisible(true);
		System.out.println("Started Generating Schedules");
		final long startTime = System.currentTimeMillis();
		// Start generating schedules
		generator = AppData.generateSchedules(schedules -> {
			// Generating schedules is complete
			if(schedules == null) {
				return;
			}
			System.out.println("Generated schedules in " + (System.currentTimeMillis() - startTime) + "ms" );
			// Show schedules
			loadingPane.setVisible(false);
			this.schedules = schedules;
			this.index = 0;
			showSchedule();
		},
		(ex) -> {
			// An exception occurred while generating schedules
			GUIUtils.showError("Unexpected output",
					"An error occurred while generating schedules. Please check your course and TA/GA "
					+ "information for any errors.\nError: " + ex.getMessage());
			SceneManager.showScene("dashboard");
		});
	}
	
	/**
	 * Cancels generating schedules and returns to the dashboard scene.
	 * 
	 * @param event The event that triggered this method.
	 * @see SceneManager
	 */
	@FXML
	public void cancelButton(ActionEvent event) {
		if (generator != null) {
			generator.cancel(true);
		}
		SceneManager.showScene("dashboard");
	}

	/**
	 * Displays the next schedule in the list or cycles back to the beginning.
	 * 
	 * @param event The event that triggered this method.
	 */
	@FXML
	public void nextSchedule(ActionEvent event) {		
		if(!validateDisplay()) {
			return;
		}
		index = (index + 1) % schedules.size();
		showSchedule();
	}
	
	/**
	 * Displays the previous schedule in the list or cycles to the end of the list.
	 * 
	 * @param event The event that triggered this method.
	 */
	@FXML
	public void previousSchedule(ActionEvent event) {
		if(!validateDisplay()) {
			return;
		}
		index = (index - 1 + schedules.size()) % schedules.size();
		showSchedule();
	}
	
	/**
	 * Opens a new window (if not already open) that shows the errors scene.
	 * 
	 * @param event The event that triggered this method.
	 */
	@FXML
	private void showErrors(ActionEvent event) {
		if(Window.getWindows().size() > 1) {
			Window.getWindows().get(1).requestFocus();
			return;
		}
		try {
			Parent root = FXMLLoader.load(SceneManager.class.getClassLoader().getResource("errors.fxml"));
			Stage stage = new Stage();
            stage.setTitle("Schedule Errors");
            stage.getIcons().add(new Image("/icon.png"));
            stage.setScene(new Scene(root));
            stage.initOwner(Window.getWindows().get(0));
            stage.show();
		} catch (IOException e) {
			System.err.println("Error loading errors.fxml");
			e.printStackTrace();
		}
	}
 	
	/**
	 * Populates the tables with information from the current index.
	 */
	private void showSchedule() {
		final Schedule schedule = schedules.get(index);
		courseTable.setItems(FXCollections.observableArrayList(schedule.getScheduledActivities()));
		taTable.setItems(FXCollections.observableArrayList(schedule.getActivitiesByTA()));
		GUIUtils.autoResizeColumns(taTable);
		scheduleNum.setText("Schedule " + (index + 1) + " of " + schedules.size());
		ErrorsController.setData(schedule.getError(), schedule.getErrorLog());
	}
	
	/**
	 * Validates that schedules were generated.
	 * 
	 * @return True if schedules were generated.
	 */
	private boolean validateDisplay() {
		String errorMessage = null;
		
		if (schedules == null || schedules.isEmpty())
			errorMessage = "No schedules available to be displayed.";
		
		if(errorMessage != null) {
			GUIUtils.showError("Unexpected output", errorMessage);
		}
		
		return errorMessage == null;
	}
}
