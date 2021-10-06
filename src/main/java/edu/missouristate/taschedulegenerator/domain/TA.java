package edu.missouristate.taschedulegenerator.domain;

import java.util.List;

public class TA {
    public String name;
    public boolean isGA;
    public int maxHours;
    public List<TimeBlock> notAvailable;

    TA(String name, boolean isGA, int maxHours, List<TimeBlock> notAvailable){
        this.name = name;
        this.isGA = isGA;
        this.maxHours = maxHours;
        this.notAvailable = notAvailable;
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
    public int getMaxHours(){return this.maxHours;}
    public void setMaxHours(int maxHours){
        this.maxHours = maxHours;
    }
    public List<TimeBlock> getNotAvailable(){
        return this.notAvailable;
    }
    public void setNotAvailable(List<TimeBlock> notAvailable){this.notAvailable = notAvailable;}
}
