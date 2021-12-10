/*
 * MIT License
 * 
 * Copyright (c) 2021 Missouri State University
 * 
 * See LICENSE in the project's root directory for full license details
 * 
 */
package edu.missouristate.taschedulegenerator.domain;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a time duration that has a start time, end time, and days of the week.
 * 
 * SDD 2.1
 * 
 * @author Cody Sullins
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeBlock {
	
	/**
	 * The start time
	 */
	private LocalTime startTime;
	/**
	 * The end time
	 */
	private LocalTime endTime;
	/**
	 * The days of the week
	 */
	private List<DayOfWeek> days;
    
	/**
	 * Checks if this TimeBlock overlaps with another TimeBlock.
	 * @param t The Timeblock to check if overlapping.
	 * @return True if this TimeBlock and the one provided overlap.
	 */
    public boolean intersects(TimeBlock t) {
    	// To overlap there has to be at least one day in common
        boolean dayOverlap = days.stream().anyMatch(day -> t.days.contains(day));
        if(dayOverlap) {
        	// Check if starts between other start/end times or ends between other start/end times
            return startTime.compareTo(t.startTime) >= 0 && startTime.compareTo(t.endTime) <= 0
                    || endTime.compareTo(t.startTime) >= 0 && endTime.compareTo(t.endTime) <= 0;
        }
        return false;
    }
    
	
  
}
