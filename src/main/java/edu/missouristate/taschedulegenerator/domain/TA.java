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
 * Represents a teaching assistant or graduate assistant.
 * 
 * @author Corey Rusher, Noah Geren
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TA {
	/**
	 * The TA's name
	 */
    private String name;
    /**
     * True if a TA, false if only a GA.
     */
    private boolean isTA;
    /**
     * The max hours per week the TA can work.
     */
    private int maxHours = 20;
    /**
     * The times the TA is unavailable to work.
     */
    private ObservableList<TimeBlock>  notAvailable = FXCollections.observableArrayList();
    
    /**
     * Changes ObservableList to List  so it can be serialized with Jackson.
     * 
     * @return A List of the times unavailable
     */
    @JsonGetter("notAvailable")
    public List<TimeBlock>  serializeNotAvailable() {
    	return Collections.unmodifiableList(notAvailable);
    }
    
    /**
     * Changes List  to ObservableList  so it can be deserialized by Jackson.
     * 
     * @param notAvailable The List  of the times unavailable that needs to be converted to ObservableList 
     */
    @JsonSetter("notAvailable")
    public void deserializeNotAvailable(List<TimeBlock>  notAvailable) {
    	this.notAvailable = FXCollections.observableArrayList(notAvailable);
    }
}
