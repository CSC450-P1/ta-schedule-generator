/*
Rev Name   Date      Description

*/

package edu.missouristate.taschedulegenerator.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

/** JavaDoc comment for public class CoursesAndTAs
*/
public class CoursesAndTAs {
	
	private List<Course> courses;
	
	private List<TA> tas;

}
