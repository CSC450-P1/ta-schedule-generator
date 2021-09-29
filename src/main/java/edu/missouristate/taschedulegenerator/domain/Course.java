package edu.missouristate.taschedulegenerator.domain;

public class Course {
    public String name;
    public String instructorName;

    Course(String name, String instructorName){
        this.name = name;
        this.instructorName = instructorName;
    }

    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getInstructorName(){
        return this.instructorName;
    }
    public void setInstructorName(String instructorName){
        this.instructorName = instructorName;
    }
}
