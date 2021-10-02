package edu.missouristate.taschedulegenerator.controllers;

import java.util.HashMap;
import java.util.Map;

import edu.missouristate.taschedulegenerator.util.ViewLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneController {
	
	public static final SceneController INSTANCE = new SceneController();
	
	private Stage primaryStage;
	private Map<String, Parent> scenes = new HashMap<>();
	
	private SceneController() { }
	
	public void init(final Stage primaryStage, final String ... scenes) {
		this.primaryStage = primaryStage;
		for(final String scene : scenes) {
			this.scenes.put(scene, ViewLoader.loadView(scene));
		}
		final Parent root = this.scenes.get(scenes[0]);
		primaryStage.setScene(new Scene(root));
	}
	
	public void showScene(final String scene) {
		final Parent root = scenes.get(scene);
		primaryStage.getScene().setRoot(root);
	}

}
