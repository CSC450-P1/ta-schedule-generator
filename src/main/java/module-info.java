module edu.missouristate.taschedulegenerator {
	requires transitive javafx.controls;
	requires javafx.fxml;
	requires static lombok;
	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.annotation;
	requires com.fasterxml.jackson.databind;
	exports edu.missouristate.taschedulegenerator;
	exports edu.missouristate.taschedulegenerator.controllers;
}