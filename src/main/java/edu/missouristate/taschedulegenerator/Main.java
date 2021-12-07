/*
Rev Name   Date      Description

*/

package edu.missouristate.taschedulegenerator;

import edu.missouristate.taschedulegenerator.util.AppData;
import edu.missouristate.taschedulegenerator.util.SceneManager;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/** JavaDoc comment for public class Main
*/
public class Main extends Application {
	
	/** JavaDoc comment for public method main
	*/
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	/** JavaDoc comment for public method start
	*/
	public void start(final Stage primaryStage) throws Exception {
		primaryStage.setTitle("TA Schedule Generator");
		primaryStage.getIcons().add(new Image("/icon.png"));
		AppData.load();
		SceneManager.init(primaryStage, "dashboard", "courseInfo", "taInfo", "schedules");
		primaryStage.show();
	}
}
