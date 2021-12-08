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
@NoArgsConstructor
@AllArgsConstructor

/** JavaDoc comment for public class TA
*/
public class TA {
    private String name;
    private boolean isTA;
    private int maxHours = 20;
    private ObservableList<TimeBlock> notAvailable = FXCollections.observableArrayList();
    
    @JsonGetter("notAvailable")
    public List<TimeBlock> serializeNotAvailable() {
    	return Collections.unmodifiableList(notAvailable);
    }
    
    @JsonSetter("notAvailable")
    public void deserializeNotAvailable(List<TimeBlock> notAvailable) {
    	this.notAvailable = FXCollections.observableArrayList(notAvailable);
    }
}
