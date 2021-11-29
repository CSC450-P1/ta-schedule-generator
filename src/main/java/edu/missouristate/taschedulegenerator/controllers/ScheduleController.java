package edu.missouristate.taschedulegenerator.controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

public class ScheduleController implements Controller < Void > , Initializable {

    @FXML
    private TableView < Schedule.ScheduledTA > taTable;

    @FXML
    private TableView < Schedule.ScheduledActivity > courseTable;

    private List < Schedule > schedules;

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
        } else if (taTable.getItems().size() > 0 && courseTable.getItems().size() > 0) {

            // Data has been processed 
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet spreadsheet = workbook.createSheet("TA Assignment");

            XSSFRow row = spreadsheet.createRow(0);

            // Setting the TA Table Column values
            for (int j = 0; j < taTable.getColumns().size(); j++) {
                row.createCell(j).setCellValue(taTable.getColumns().get(j).getText());
            }

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


            List < String > courses = new ArrayList < String > ();

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

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                XSSFSheet sheet = workbook.getSheetAt(i);
                if (sheet.getPhysicalNumberOfRows() > 0) {
                    XSSFRow rowT = sheet.getRow(sheet.getFirstRowNum());
                    Iterator < Cell > cellIterator = rowT.cellIterator();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        int columnIndex = cell.getColumnIndex();
                        sheet.autoSizeColumn(columnIndex);
                    }
                }
            }

            Window current = Stage.getWindows().stream().filter(Window::isShowing).findFirst().orElse(null);
            FileChooser fChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel Workbook", "*.xlsx");
            fChooser.getExtensionFilters().add(extFilter);
            fChooser.setInitialFileName("Generated TA Schedule " + (index + 1));
            File tempFile = fChooser.showSaveDialog(current);

            if (tempFile != null) {
                try (FileOutputStream out = new FileOutputStream(tempFile.getAbsolutePath())) {
                    workbook.write(out);
                } catch (IOException e) {
                    Alert errorAlert = new Alert(AlertType.ERROR);
                    errorAlert.setTitle("Error Occured During Saving");
                    errorAlert.setHeaderText("Error saving schedule, please close any open schedules and try again.");
                    errorAlert.showAndWait();
                    //System.out.println(e);
                }
            }

        }

    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        taTable.setEditable(true);
        //TA Table
        final TableColumn < Schedule.ScheduledTA, String > taColumn = new TableColumn < > ("TA");
        taColumn.setCellFactory(TextFieldTableCell. < Schedule.ScheduledTA > forTableColumn());
        taColumn.setCellValueFactory(cell -> {
            return new SimpleStringProperty(cell.getValue().getTA().getName());
        });
        taTable.getColumns().add(taColumn);

        final TableColumn < Schedule.ScheduledTA, String > maxHoursColumn = new TableColumn < > ("Max Hours");
        maxHoursColumn.setCellFactory(TextFieldTableCell. < Schedule.ScheduledTA > forTableColumn());
        maxHoursColumn.setCellValueFactory(cell -> {
            return new SimpleStringProperty(String.valueOf(cell.getValue().getTA().getMaxHours()));
        });
        taTable.getColumns().add(maxHoursColumn);

        final TableColumn < Schedule.ScheduledTA, String > assignedHoursColumn = new TableColumn < > ("Assigned Hours");
        assignedHoursColumn.setCellFactory(TextFieldTableCell. < Schedule.ScheduledTA > forTableColumn());
        assignedHoursColumn.setCellValueFactory(cell -> {
            int assignedHours = 0;
            for (final ScheduledActivity activity: cell.getValue().getActivities()) {
                assignedHours += activity.getHours();
            }
            return new SimpleStringProperty(String.valueOf(assignedHours));
        });
        taTable.getColumns().add(assignedHoursColumn);

        final TableColumn < Schedule.ScheduledTA, String > courseColumn = new TableColumn < > ("Courses");
        courseColumn.setCellFactory(TextFieldTableCell. < Schedule.ScheduledTA > forTableColumn());
        courseColumn.setCellValueFactory(cell -> {
            return new SimpleStringProperty(
                cell.getValue().getActivities()
                .stream()
                .map(a -> String.format("%s %s", a.getActivity().getCourse().getCourseCode(), a.getActivity().getName()))
                .collect(Collectors.joining(", ")));
        });
        taTable.getColumns().add(courseColumn);

        //Course Table
        final TableColumn < Schedule.ScheduledActivity, String > courseTableColumn = new TableColumn < > ("Course");
        courseTableColumn.setCellFactory(TextFieldTableCell. < ScheduledActivity > forTableColumn());
        courseTableColumn.setCellValueFactory(cell -> {
            return new SimpleStringProperty(cell.getValue().getActivity().getCourse().getCourseCode());
        });
        courseTable.getColumns().add(courseTableColumn);

        final TableColumn < Schedule.ScheduledActivity, String > activityColumn = new TableColumn < > ("Activity");
        activityColumn.setCellFactory(TextFieldTableCell. < ScheduledActivity > forTableColumn());
        activityColumn.setCellValueFactory(cell -> {
            return new SimpleStringProperty(cell.getValue().getActivity().getName());
        });
        courseTable.getColumns().add(activityColumn);

        final TableColumn < Schedule.ScheduledActivity, String > hoursNeededColumn = new TableColumn < > ("Hours Needed");
        hoursNeededColumn.setCellFactory(TextFieldTableCell. < ScheduledActivity > forTableColumn());
        hoursNeededColumn.setCellValueFactory(cell -> {
            return new SimpleStringProperty(String.valueOf(cell.getValue().getActivity().getHoursNeeded()));
        });
        courseTable.getColumns().add(hoursNeededColumn);

        final TableColumn < Schedule.ScheduledActivity, String > hoursAssignedColumn = new TableColumn < > ("Hours Assigned");
        hoursAssignedColumn.setCellFactory(TextFieldTableCell. < ScheduledActivity > forTableColumn());
        hoursAssignedColumn.setCellValueFactory(cell -> {
            return new SimpleStringProperty(String.valueOf(cell.getValue().getHours()));
        });
        courseTable.getColumns().add(hoursAssignedColumn);

        final TableColumn < Schedule.ScheduledActivity, String > taTableColumn = new TableColumn < > ("TA");
        taTableColumn.setCellFactory(TextFieldTableCell. < ScheduledActivity > forTableColumn());
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
            System.out.println("Generated " + schedules.size() + " schedules in " + (System.currentTimeMillis() - startTime) + "ms");
            System.out.println("Best Generated Schedule:");
            System.out.println(String.format("%s %10s %s %s", "Course", "Activity", "TA", "Hours"));
            final Schedule bestSchedule = schedules.get(0);

            List < Schedule.ScheduledTA > scheduledTAs = bestSchedule.getActivitiesByTA();
            taTable.setItems(FXCollections.observableArrayList(scheduledTAs));
            List < Schedule.ScheduledActivity > scheduledActivities = bestSchedule.getScheduledActivities();
            courseTable.setItems(FXCollections.observableArrayList(scheduledActivities));

            System.out.println("Error Total: " + bestSchedule.getError());
            for (final ScheduledActivity activity: bestSchedule.getScheduledActivities()) {
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
        if (!validateDisplay()) {
            return;
        }
        index = (index + 1) % schedules.size();
        showSchedule();
    }

    @FXML
    public void previousSchedule(ActionEvent event) {
        if (!validateDisplay()) {
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

        if (errorMessage != null) {
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