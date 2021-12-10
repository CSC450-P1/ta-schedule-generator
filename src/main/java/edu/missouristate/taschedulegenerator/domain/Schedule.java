/*
 * MIT License
 * 
 * Copyright (c) 2021 Missouri State University
 * 
 * See LICENSE in the project's root directory for full license details
 * 
 */
package edu.missouristate.taschedulegenerator.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Data;

/**
 * Represents a schedule of courses and TAs.
 * 
 * SDD 2.1
 * 
 * @author Noah Geren
 *
 */
@Data
public class Schedule {
	
	/**
	 * List of scheduled activities.
	 */
	private final List<ScheduledActivity> scheduledActivities;
	
	/**
	 * The error value of this schedule.
	 */
	private int error;
	
	/**
	 * The error messages describing what is wrong with this schedule.
	 */
	private final List<String> errorLog = new ArrayList<>();
	
	/**
	 * Converts the list of scheduled activities to a list of scheduled TAs.
	 * 
	 * @return List of scheduled TAs.
	 */
	public ObservableList<ScheduledTA> getActivitiesByTA() {
		final Map<TA, ScheduledTA> activitiesByTA = new HashMap<>();
		// Go through each activity
		for(final ScheduledActivity activity : scheduledActivities) {
			final TA ta = activity.ta;
			// If TA hasn't been added yet then create a new ScheduledTA
			final ScheduledTA scheduledTA = activitiesByTA.getOrDefault(ta, new ScheduledTA(ta, new ArrayList<>()));
			scheduledTA.activities.add(activity);
			activitiesByTA.put(ta, scheduledTA);
		}
		// Collect all ScheduledTAs
		List<ScheduledTA> result = new ArrayList<>(activitiesByTA.values());
		Collections.sort(result, (a,b) -> {
			return a.ta.getName().compareToIgnoreCase(b.ta.getName());
		});
		return FXCollections.observableArrayList(result);
	}

	/**
	 * Represents a scheduled activity which has an assigned TA and hours.
	 * 
	 * @author Noah Geren
	 *
	 */
	@Data
	public static class ScheduledActivity {
		
		/**
		 * The scheduled activity.
		 */
		private final Activity activity;
		/**
		 * The TA assigned to the activity.
		 */
		private final TA ta;
		/**
		 * The hours assigned to the activity.
		 */
		private final int hours;
		
		public TA getTA() {
			return ta;
		}
		
	}
	
	/**
	 * Represents a scheduled TA which has assigned activities.
	 * 
	 * @author Noah Geren
	 *
	 */
	@Data
	public static class ScheduledTA {
		/**
		 * The scheduled TA
		 */
		private final TA ta;
		/**
		 * The activities assigned to the TA
		 */
		private final List<ScheduledActivity> activities;
		
		public TA getTA() {
			return ta;
		}
		
	}
	
}
