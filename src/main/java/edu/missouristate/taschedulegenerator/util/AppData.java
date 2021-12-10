/*
 * MIT License
 * 
 * Copyright (c) 2021 Missouri State University
 * 
 * See LICENSE in the project's root directory for full license details
 * 
 */
package edu.missouristate.taschedulegenerator.util;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import edu.missouristate.taschedulegenerator.algorithm.TAScheduler;
import edu.missouristate.taschedulegenerator.domain.Course;
import edu.missouristate.taschedulegenerator.domain.CoursesAndTAs;
import edu.missouristate.taschedulegenerator.domain.Schedule;
import edu.missouristate.taschedulegenerator.domain.TA;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * Stores all data that is needed for the application to run. Also provides methods for saving/loading app data and for generating schedules.
 * 
 * SDD 3.2.2
 * 
 * @author Noah Geren
 *
 */
public class AppData {
	
	/**
	 * Used for time selection options.
	 */
	public static final ObservableList<String> TIMES;
	/**
	 * Used to format time selection options and to parse selected time options.
	 */
	public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");
	/**
	 * Start time for time selection options.
	 */
	private static final LocalTime START_TIME = LocalTime.of(8, 0);
	/**
	 * End time for time selection options.
	 */
	private static final LocalTime END_TIME = LocalTime.of(20, 0);
	
	/**
	 * The list of courses the app is using.
	 */
	private static ObservableList<Course> courses = FXCollections.observableArrayList();
	/**
	 * The list of TAs the app is using.
	 */
	private static ObservableList<TA> tas = FXCollections.observableArrayList();
	
	/**
	 * The file used to save/load course and TA data.
	 */
	private static final File SAVE_FILE = new File("session.json");
	/**
	 * Used to convert objects into JSON.
	 */
	private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());	

	static {
		// Generate time selection options
		final ObservableList<String> times = FXCollections.observableArrayList();
		for(LocalTime time = START_TIME; time.compareTo(END_TIME) <= 0; time=time.plusMinutes(15)) {
			times.add(TIME_FORMATTER.format(time));
		}
		TIMES = FXCollections.unmodifiableObservableList(times);
	}

	/**
	 * This class should only be accessed statically.
	 */
	private AppData() {}

	/**
	 * @return The list of courses the app is using.
	 */
	public static ObservableList<Course> getCourses() {
		return courses;
	}
	
	/**
	 * @return The list of TAs the app is using.
	 */
	public static ObservableList<TA> getTAs() {
		return tas;
	}
	
	/**
	 * Loads courses and TAs from session.json and adds them to the AppData lists.
	 * 
	 * @see SAVE_FILE
	 * @see MAPPER
	 */
	public static void load() {
		// If save file exists then load it
		if(SAVE_FILE.exists()) {
			try {
				// Load as CoursesAndTAs so lists are generic
				final CoursesAndTAs data = MAPPER.readValue(SAVE_FILE, CoursesAndTAs.class);
				// Change lists to be observable
				courses = FXCollections.observableArrayList(data.getCourses());
				tas = FXCollections.observableArrayList(data.getTas());
			} catch (IOException e) {
				System.err.println("Could not load save file " + SAVE_FILE);
				e.printStackTrace();
			}
		}
		// Add observers to automatically save on list changes
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
	
	/**
	 * Saves courses and TAs to session.json using the JSON format.
	 * 
	 * @see SAVE_FILE
	 * @see MAPPER
	 */
	public static void save() {
		try {
			// Save as CoursesAndTAs so lists are generic
			MAPPER.writeValue(SAVE_FILE, new CoursesAndTAs(courses, tas));
		} catch (IOException e) {
			System.err.println("Could not save file " + SAVE_FILE.getAbsolutePath());
			e.printStackTrace();
		}
	}
	
	/**
	 * Start generating schedules using the courses and TAs in AppData.
	 * @param callback The callback function for when generating schedules completes.
	 * @param errorCallback The error callback function if an uncaught exception occurs while generating schedules.
	 * @return A Future object that can be used to cancel generating schedules.
	 */
	public static Future<?> generateSchedules(final Consumer<List<Schedule>> callback, final Consumer<Exception> errorCallback) {
		return TAScheduler.schedule(tas, courses,
				// Use Platform.runLater so it runs on the JavaFX thread
				schedules -> {
					Platform.runLater(() -> callback.accept(schedules));
				},
				e -> Platform.runLater(() -> errorCallback.accept(e)));
	}

}
