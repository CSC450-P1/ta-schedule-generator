/*
 * MIT License
 * 
 * Copyright (c) 2021 Missouri State University
 * 
 * See LICENSE in the project's root directory for full license details
 * 
 */
package edu.missouristate.taschedulegenerator;

import edu.missouristate.taschedulegenerator.util.AppData;
import edu.missouristate.taschedulegenerator.util.SceneManager;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * The starting point for the entire application. Loads scenes and shows the window.
 * 
 * @author Noah Geren, Cody Sullins
 *
 */
public class Main extends Application {
	
	/**
	 * The first method called in the application.
	 * 
	 * @param args Arguments passed to JavaFX.
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Sets up the GUI window, loads session.json, and loads the necessary scenes.
	 */
	@Override
	public void start(final Stage primaryStage) throws Exception {
		primaryStage.setTitle("TA Schedule Generator");
		primaryStage.getIcons().add(new Image("/icon.png"));
		AppData.load();
		SceneManager.init(primaryStage, "dashboard", "courseInfo", "taInfo", "schedules");
		primaryStage.show();
		primaryStage.setMinWidth(primaryStage.getWidth());
		primaryStage.setMinHeight(primaryStage.getHeight());
	}
}
