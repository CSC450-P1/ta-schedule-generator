package edu.missouristate.taschedulegenerator.domain;

import java.util.List;

public class Course {
    public String courseCode;
    public String instructorName;
    public List<Activity> activities;

    Course(String courseCode, String instructorName, List<Activity> activities){
        this.courseCode = courseCode;
        this.instructorName = instructorName;
        this.activities = activities;
    }

    public String getCourseCode(){
        return this.courseCode;
    }
    public void setCourseCode(String courseCode){
        this.courseCode = courseCode;
    }
    public String getInstructorName(){
        return this.instructorName;
    }
    public void setInstructorName(String instructorName){
        this.instructorName = instructorName;
    }
    public List<Activity> getActivities(){return this.activities;}
    public void setActivities(List<Activity> activities){this.activities = activities;}
}
