package edu.missouristate.taschedulegenerator.util;
import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class ViewLoader {

	public static Parent loadView(String name) {
		try {
			return FXMLLoader.load(ViewLoader.class.getClassLoader().getResource(name + ".fxml"));
		} catch (IOException e) {
			System.err.println("Unable to load view " + name);
			e.printStackTrace();
			return null;
		}
	}
	
}
