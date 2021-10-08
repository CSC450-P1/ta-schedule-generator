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
    public String courseCode;
    public String instructorName;
    public List<Activity> activities = new ArrayList<>();
}
