package edu.missouristate.taschedulegenerator.controllers;

import java.net.URL;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import edu.missouristate.taschedulegenerator.util.SceneManager;
import edu.missouristate.taschedulegenerator.util.SceneManager.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;

public class TimeUnavailableController implements Controller<String>, Initializable {
	
	
	@FXML
	private ComboBox<String> startSelection;
	
	@FXML
	private ComboBox<String> endSelection;
	
	@FXML
	private RadioButton Monday, Tuesday, Wednesday, Thursday, Friday;
	
	private List<RadioButton> daysOfWeek = new ArrayList<RadioButton>();
	
	
	@FXML
	public void initialize(URL location, ResourceBundle resources) {
		// Found list of times online - definitely better way to create these but filler for now
		List<String> times = Arrays.asList("8:00 AM", "8:15 AM", "8:30 AM", "8:45 AM", "9:00 AM", "9:15 AM", "9:30 AM", "9:45 AM",
				"10:00 AM", "10:15 AM", "10:30 AM", "10:45 AM", "11:00 AM", "11:15 AM", "11:30 AM", "11:45 AM", "12:00 PM",
				"12:15 PM", "12:30 PM", "12:45 PM", "1:00 PM", "1:15 PM", "1:30 PM", "1:45 PM", "2:00 PM", "2:15 PM",
				"2:30 PM", "2:45 PM", "3:00 PM", "3:15 PM", "3:30 PM", "3:45 PM", "4:00 PM", "4:15 PM", "4:30 PM",
				"4:45 PM", "5:00 PM", "5:15 PM", "5:30 PM", "5:45 PM", "6:00 PM", "6:15 PM", "6:30 PM", "6:45 PM", "7:00 PM",
				"7:15 PM", "7:30 PM", "7:45 PM", "8:00 PM"
				);
		startSelection.getItems().removeAll(startSelection.getItems());
		startSelection.getItems().addAll(times);

		endSelection.getItems().removeAll(endSelection.getItems());
		endSelection.getItems().addAll(times);
		
        
        daysOfWeek.addAll(Arrays.asList(Monday, Tuesday, Wednesday, Thursday, Friday));
        
        
        
        //System.out.println(testBlock);
        
        
       
		//ysOfWeek.addAll(test);
	   //   System.out.println(test.get(i).getId());
	    
	    //ystem.out.println(test.get(i).getTypeSelector());
	    
		//daysofWeek = 
		
	}
	
	
	
	@FXML
	public void addTimeUnavailable(ActionEvent event) {
		//Checking which days are selected
		for(int i = 0; i < daysOfWeek.size(); i++) {
			if(daysOfWeek.get(i).isSelected() == true) {
				System.out.println(daysOfWeek.get(i));
			}
		}
		//Getting time on selected days unavailable
		LocalTime startTime = getLocalTime(startSelection);
		LocalTime endTime = getLocalTime(endSelection);
		
		System.out.println(startTime);
		System.out.println(endTime);
		
	
	}
	
	@FXML
	public void saveTAInfo(ActionEvent event) {
		System.out.print("Saving the info from the table");
		//SceneManager.showScene("taInfo");
	}
	
	
	@FXML
	public void clearCurrentSelection(ActionEvent event) {
		//Resetting M-F selections if selected
		for(int i = 0; i < daysOfWeek.size(); i++) {
			if(daysOfWeek.get(i).isSelected() == true) {
				daysOfWeek.get(i).setSelected(false);
			}
		}
		//Resetting ComboBox of Time Selections
		startSelection.getSelectionModel().clearSelection();
		endSelection.getSelectionModel().clearSelection();
		
	}

	
	@FXML
	public void cancel(ActionEvent event) {
		SceneManager.showScene("taInfo");
	}
	//Helper function
	public LocalTime getLocalTime(ComboBox<String> timeSelection) {
		String[] hourandMinute = timeSelection.getValue().split(":");
		String hour = hourandMinute[0];
		String minute = hourandMinute[1];
		minute = minute.split(" ")[0];
		
		LocalTime localTime = LocalTime.of(Integer.parseInt(hour), Integer.parseInt(minute), 00);
		
		return localTime;
		
	}
	
	@Override
	public void initData(String data) {
		// TODO Auto-generated method stub
	}
	
	
	


	
	
}
