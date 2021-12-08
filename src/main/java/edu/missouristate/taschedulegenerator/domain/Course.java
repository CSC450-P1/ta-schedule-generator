package edu.missouristate.taschedulegenerator.domain;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a university course.
 * 
 * @author Noah Geren, Corey Rusher
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Course {
	/**
	 * The course code.
	 */
    private String courseCode;
    /**
     * The instructor's name.
     */
    private String instructorName;
    /**
     * This course's activities.
     */
    private ObservableList<Activity> activities = FXCollections.observableArrayList();
    
    /**
     * Changes ObservableList to List so it can be serialized with Jackson.
     * 
     * @return A List of the activities
     */
    @JsonGetter("activities")
    public List<Activity> serializeActivities() {
    	return Collections.unmodifiableList(activities);
    }
    
    /**
     * Changes List to ObservableList so it can be deserialized by Jackson.
     * 
     * @param activities The List of the activities that needs to be converted to ObservableList
     */
    @JsonSetter("activities")
    public void deserializeActivities(List<Activity> activities) {
    	this.activities = FXCollections.observableArrayList(activities);
    }
}
