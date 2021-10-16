package edu.missouristate.taschedulegenerator.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Course {
    private String courseCode;
    private String instructorName;
    private List<Activity> activities = new ArrayList<>();
}
