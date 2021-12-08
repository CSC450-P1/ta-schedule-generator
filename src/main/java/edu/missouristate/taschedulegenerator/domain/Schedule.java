/*
Rev Name   Date      Description

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

@Data

/** JavaDoc comment for public class Schedule
*/
public class Schedule {
	
	private final List<ScheduledActivity> scheduledActivities;
	
	private int error;
	
	private final List<String> errorLog = new ArrayList<>();
	
	public ObservableList<ScheduledTA> getActivitiesByTA() {
		final Map<TA, ScheduledTA> activitiesByTA = new HashMap<>();
		for(final ScheduledActivity activity : scheduledActivities) {
			final TA ta = activity.ta;
			final ScheduledTA scheduledTA = activitiesByTA.getOrDefault(ta, new ScheduledTA(ta, new ArrayList<>()));
			scheduledTA.activities.add(activity);
			activitiesByTA.put(ta, scheduledTA);
		}
		List<ScheduledTA> result = new ArrayList<>(activitiesByTA.values());
		Collections.sort(result, (a,b) -> {
			return a.ta.getName().compareToIgnoreCase(b.ta.getName());
		});
		return FXCollections.observableArrayList(result);
	}

	@Data

	/** JavaDoc comment for public class ScheduleActivity
	*/
	public static class ScheduledActivity {
		
		private final Activity activity;
		private final TA ta;
		private final int hours;
		
		public TA getTA() {
			return ta;
		}
		
	}
	
	@Data

	/** JavaDoc comment for public class ScheduledTA
	*/
	public static class ScheduledTA {
		
		private final TA ta;
		private final List<ScheduledActivity> activities;
		
		public TA getTA() {
			return ta;
		}
		
	}
	
}
