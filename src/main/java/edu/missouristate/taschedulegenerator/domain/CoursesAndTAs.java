package edu.missouristate.taschedulegenerator.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the data saved to/loaded from session.json
 * 
 * @author Noah Geren
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoursesAndTAs {
	
	private List<Course> courses;
	
	private List<TA> tas;

}
