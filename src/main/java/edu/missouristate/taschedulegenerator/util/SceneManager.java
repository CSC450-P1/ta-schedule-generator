/*
Rev Name   Date      Description

*/

package edu.missouristate.taschedulegenerator.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {
	
	private static Stage primaryStage;
	private static Map<String, Parent> scenes = new HashMap<>();
	private static Map<String, Controller<?>> controllers = new HashMap<>();
	
	private SceneManager() { }
	
	public static void init(final Stage primaryStage, final String ... scenes) {
		SceneManager.primaryStage = primaryStage;
		for(final String scene : scenes) {
			final FXMLLoader loader = new FXMLLoader(SceneManager.class.getClassLoader().getResource(scene + ".fxml"));
			try {
				SceneManager.scenes.put(scene, loader.load());
				final Object controller = loader.getController();
				if(controller != null && controller instanceof Controller) {
					SceneManager.controllers.put(scene, (Controller<?>) controller);
				}
			} catch (IOException e) {
				System.err.println("ERROR - Unable to load " + scene + ".fxml");
				e.printStackTrace();
				System.exit(0);
			}
		}
		final Parent root = SceneManager.scenes.get(scenes[0]);
		primaryStage.setScene(new Scene(root));
	}
	
	public static void showScene(final String scene) {
		final Parent root = scenes.get(scene);
		primaryStage.getScene().setRoot(root);
	}
	
	public static <T> void showScene(final String scene, T data) {
		boolean error = false;
		try {
			@SuppressWarnings("unchecked")
			final Controller<T> controller = (Controller<T>) controllers.get(scene);
			if(controller != null) {
				controller.initData(data);
			} else {
				error = true;
			}
		} catch(ClassCastException ex) {
			error = true;
		}
		if(error) {
			final String typeName = data.getClass().getSimpleName();
			System.err.println("ERROR - Controller for " + scene + ".fxml must implement Controller<" + typeName + "> to pass data with type " + typeName + " to it.");
			System.exit(0);
		}
		
		SceneManager.showScene(scene);
	}
	
	public interface Controller<T> {
		
		void initData(T data);

		

	}

}
