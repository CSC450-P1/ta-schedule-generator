package edu.missouristate.taschedulegenerator.domain;

import java.util.List;

public class TA {
    public String name;
    public boolean isGA;
    public int maxHours;
    public List<String> courses;

    TA(String name, boolean isGA, int maxHours, List<String>courses){
        this.name = name;
        this.isGA = isGA;
        this.maxHours = maxHours;
        this.courses = courses;
    }

    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }
    public boolean getIsGA(){
        return this.isGA;
    }
    public void setIsGA(boolean isGA){
        this.isGA = isGA;
    }
    public int getMaxHours(){
        return this.maxHours;
    }
    public void setMaxHours(int maxHours){
        this.maxHours = maxHours;
    }
    public List<String> getCourses(){
        return this.courses;
    }
    public void setCourses(List<String> courses){
        this.courses = courses;
    }
}
