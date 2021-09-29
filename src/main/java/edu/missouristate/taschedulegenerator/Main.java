package edu.missouristate.taschedulegenerator;

import edu.missouristate.taschedulegenerator.util.ViewLoader;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(final Stage primaryStage) throws Exception {
		primaryStage.setTitle("Example");
		Parent root = ViewLoader.loadView("example");
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	}
}
