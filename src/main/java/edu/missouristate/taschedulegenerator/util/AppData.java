/*
Rev Name   Date      Description

*/

package edu.missouristate.taschedulegenerator.util;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
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

public class AppData {
	
	public static final ObservableList<String> TIMES;
	public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a");
	
	private static final LocalTime START_TIME = LocalTime.of(8, 0);
	private static final LocalTime END_TIME = LocalTime.of(20, 0);
	
	private static ObservableList<Course> courses = FXCollections.observableArrayList();
	private static ObservableList<TA> tas = FXCollections.observableArrayList();
	
	private static final File SAVE_FILE = new File("session.json");
	private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());	

	static {
		final ObservableList<String> times = FXCollections.observableArrayList();
		for(LocalTime time = START_TIME; time.compareTo(END_TIME) <= 0; time=time.plusMinutes(15)) {
			times.add(TIME_FORMATTER.format(time));
		}
		TIMES = FXCollections.unmodifiableObservableList(times);
	}

	
	private AppData() {}

	public static ObservableList<Course> getCourses() {
		return courses;
	}
	
	public static ObservableList<TA> getTAs() {
		return tas;
	}
	
	public static void load() {
		if(SAVE_FILE.exists()) {
			try {
				final CoursesAndTAs data = MAPPER.readValue(SAVE_FILE, CoursesAndTAs.class);
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
			MAPPER.writeValue(SAVE_FILE, new CoursesAndTAs(courses, tas));
		} catch (IOException e) {
			System.err.println("Could not save file " + SAVE_FILE.getAbsolutePath());
			e.printStackTrace();
		}
	}
	
	public static CompletableFuture<List<Schedule>> generateSchedules(Consumer<List<Schedule>> callback) {
		final CompletableFuture<List<Schedule>> future = TAScheduler.schedule(tas, courses);
		future.thenAccept(schedules -> {
			Platform.runLater(() -> callback.accept(schedules));
		});
		return future;
	}

}
