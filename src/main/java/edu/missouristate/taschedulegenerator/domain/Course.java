/*
Rev Name   Date      Description

*/

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

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Course {
    private String courseCode;
    private String instructorName;
    private ObservableList<Activity> activities = FXCollections.observableArrayList();
    
    @JsonGetter("activities")
    public List<Activity> serializeActivities() {
    	return Collections.unmodifiableList(activities);
    }
    
    @JsonSetter("activities")
    public void deserializeActivities(List<Activity> activities) {
    	this.activities = FXCollections.observableArrayList(activities);
    }
}
