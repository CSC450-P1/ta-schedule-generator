/*
Rev Name   Date      Description

*/

package edu.missouristate.taschedulegenerator.domain;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor

/** JavaDoc comment for public class TimeBlock
*/
public class TimeBlock {
	
	private LocalTime startTime;
	private LocalTime endTime;
	private List<DayOfWeek> days;
    

    public boolean intersects(TimeBlock t) {
        boolean dayOverlap = days.stream().anyMatch(day -> t.days.contains(day));
        if(dayOverlap) {
            return startTime.compareTo(t.startTime) >= 0 && startTime.compareTo(t.endTime) <= 0
                    || endTime.compareTo(t.startTime) >= 0 && endTime.compareTo(t.endTime) <= 0;
        }
        return false;
    }
    
	
  
}
