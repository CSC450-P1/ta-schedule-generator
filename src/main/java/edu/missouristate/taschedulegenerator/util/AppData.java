package edu.missouristate.taschedulegenerator.util;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.missouristate.taschedulegenerator.domain.Course;
import edu.missouristate.taschedulegenerator.domain.CoursesAndTAs;
import edu.missouristate.taschedulegenerator.domain.TA;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class AppData {
	public static final List<String> TIMES = new ArrayList<>();
	public static final LocalTime END_TIME = LocalTime.of(20, 0);
	public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");

	static {
	    for(LocalTime time = LocalTime.of(8, 0); time.compareTo(END_TIME) <= 0; time=time.plusMinutes(15)) {
	        TIMES.add(time.format(TIME_FORMATTER));
	    }
	}
	private static final String SAVE_FILE = "session.json";
	private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();
	
	private static ObservableList<Course> courses = FXCollections.observableArrayList();
	
	private static ObservableList<TA> tas = FXCollections.observableArrayList();
	
	private AppData() {}

	public static ObservableList<Course> getCourses() {
		return courses;
	}
	
	public static ObservableList<TA> getTAs() {
		return tas;
	}
	
	public static void load() {
		final File saveFile = new File(SAVE_FILE);
		if(saveFile.exists()) {
			try {
				final CoursesAndTAs data = MAPPER.readValue(saveFile, CoursesAndTAs.class);
				courses = FXCollections.observableArrayList(data.getCourses());
				tas = FXCollections.observableArrayList(data.getTas());
			} catch (IOException e) {
				System.err.println("Could not load save file " + SAVE_FILE);
				e.printStackTrace();
			}
		}
		courses.addListener(new ListChangeListener<Course>() {

			@Override
			public void onChanged(Change<? extends Course> c) {
				save();
			}
			
		});
		tas.addListener(new ListChangeListener<TA>() {

			@Override
			public void onChanged(Change<? extends TA> c) {
				save();
			}
			
		});
	}
	
	public static void save() {
		try {
			MAPPER.writeValue(new File(SAVE_FILE), new CoursesAndTAs(courses, tas));
		} catch (IOException e) {
			System.err.println("Could not save file " + SAVE_FILE);
			e.printStackTrace();
		}
	}

}
