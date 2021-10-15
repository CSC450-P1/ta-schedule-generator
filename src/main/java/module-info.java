module edu.missouristate.taschedulegenerator {
	requires transitive javafx.controls;
	requires javafx.fxml;
	requires static lombok;
	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.annotation;
	requires com.fasterxml.jackson.databind;
	requires javafx.base;
	requires javafx.graphics;
	opens edu.missouristate.taschedulegenerator.controllers to javafx.fxml;
	exports edu.missouristate.taschedulegenerator;
}