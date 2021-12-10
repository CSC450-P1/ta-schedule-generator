/*
 * MIT License
 * 
 * Copyright (c) 2021 Missouri State University
 * 
 * See LICENSE in the project's root directory for full license details
 * 
 */
package edu.missouristate.taschedulegenerator.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the data saved to/loaded from session.json
 * 
 * SDD 2.3
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
