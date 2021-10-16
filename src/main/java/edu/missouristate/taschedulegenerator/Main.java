package edu.missouristate.taschedulegenerator;

import edu.missouristate.taschedulegenerator.util.AppData;
import edu.missouristate.taschedulegenerator.util.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(final Stage primaryStage) throws Exception {
		primaryStage.setTitle("TA Schedule Generator");
		AppData.load();
		SceneManager.init(primaryStage, "dashboard", "courseInfo", "taInfo", "schedules");
		primaryStage.show();
	}
}
