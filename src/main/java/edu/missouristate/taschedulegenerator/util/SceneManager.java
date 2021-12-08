package edu.missouristate.taschedulegenerator.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Utility class that manages loading and swapping scenes.
 * 
 * @author Noah Geren
 *
 */
public class SceneManager {
	/**
	 * The stage the scenes are being shown on.
	 */
	private static Stage primaryStage;
	/**
	 * Map of scene names to their root components.
	 */
	private static Map<String, Parent> scenes = new HashMap<>();
	/**
	 * Map of scene names to their controller objects.
	 */
	private static Map<String, Controller<?>> controllers = new HashMap<>();
	
	/**
	 * This class should only be accessed statically.
	 */
	private SceneManager() { }
	
	/**
	 * Loads the given scene FMXL files and shows the first listed scene.
	 * 
	 * @param primaryStage The stage to show scenes on.
	 * @param scenes The list of scene FXML files that should be loaded.
	 */
	public static void init(final Stage primaryStage, final String ... scenes) {
		SceneManager.primaryStage = primaryStage;
		for(final String scene : scenes) {
			// Load each scene
			final FXMLLoader loader = new FXMLLoader(SceneManager.class.getClassLoader().getResource(scene + ".fxml"));
			try {
				SceneManager.scenes.put(scene, loader.load());
				// Get the controller for the loaded scene
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
		// Show the first listed scene
		final Parent root = SceneManager.scenes.get(scenes[0]);
		primaryStage.setScene(new Scene(root));
	}
	
	/**
	 * Shows a scene that was previously loaded.
	 * 
	 * @param scene The name of the scene to show.
	 */
	public static void showScene(final String scene) {
		final Parent root = scenes.get(scene);
		primaryStage.getScene().setRoot(root);
	}
	
	/**
	 * Shows a scene that was previously loaded and passes provided data to the scene controller's initData method.
	 * @param <T> The type of the data being passed and the controller it is being passed to.
	 * @param scene The name of the scene to show.
	 * @param data The data that will be passed to the scene controller's initData method.
	 */
	public static <T> void showScene(final String scene, T data) {
		boolean error = false;
		try {
			@SuppressWarnings("unchecked")
			// Get the controller for the scene
			final Controller<T> controller = (Controller<T>) controllers.get(scene);
			if(controller != null) {
				controller.initData(data);
			} else {
				error = true;
			}
		} catch(ClassCastException ex) {
			error = true;
		}
		if(error) { // If the controller could not be loaded then show an error
			final String typeName = data.getClass().getSimpleName();
			System.err.println("ERROR - Controller for " + scene + ".fxml must implement Controller<" + typeName + "> to pass data with type " + typeName + " to it.");
			System.exit(0);
		}
		
		SceneManager.showScene(scene);
	}
	
	/**
	 * Interface used to pass data to controllers before showing them.
	 * 
	 * @author Noah Geren
	 *
	 * @param <T> The type of data that can be used to initialize the controller.
	 */
	public interface Controller<T> {
		
		/**
		 * Used to initialize the data in the controller before it is shown.
		 * 
		 * @param data The data used in initialization.
		 */
		void initData(T data);

		

	}

}
