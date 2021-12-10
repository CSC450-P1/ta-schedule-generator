/*
 * MIT License
 * 
 * Copyright (c) 2021 Missouri State University
 * 
 * See LICENSE in the project's root directory for full license details
 * 
 */
package edu.missouristate.taschedulegenerator.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Represents a course activity.
 * 
 * SDD 2.1
 * 
 * @author Carlos Izaguirre
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Activity {
	
	/**
	 * The activity name. Ex. Grading or Lab
	 */
	private String name;
	
	/**
	 * True if the activity must be assisted by a TA (rather than only a GA)
	 */
	private boolean mustBeTA;
	
	/**
	 * The estimated weekly hours needed.
	 */
	private int hoursNeeded;

	/**
	 * The time that a TA must be available for.
	 */
	private TimeBlock time;
	
	/**
	 * The course that this activity belongs to.
	 */
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	private Course course;
}
