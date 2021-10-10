package edu.missouristate.taschedulegenerator.domain;

import javafx.beans.property.SimpleStringProperty;

public class TimeBlock {
	private SimpleStringProperty dayUnavailable;
	public SimpleStringProperty beginTimeUnavailable;
	public SimpleStringProperty endTimeUnavailable;
	
	
	public TimeBlock(String dayUnavailable, String beginTime, String endTime) {
		this.dayUnavailable = new SimpleStringProperty(dayUnavailable);
		this.beginTimeUnavailable = new SimpleStringProperty(beginTime);
		this.endTimeUnavailable = new SimpleStringProperty(endTime);
		// TODO Auto-generated constructor stub
	}
	public TimeBlock() {
		// TODO Auto-generated constructor stub
	}
	public String getDayUnavailable(){
        return dayUnavailable.get();
    }
    public void setDayUnavailable(String dayUnavailable){
        this.dayUnavailable = new SimpleStringProperty(dayUnavailable);
    }
    public String getBeginTimeUnavailable(){
        return beginTimeUnavailable.get();
    }
    public void setBeginTimeUnavailable(String t){
        this.beginTimeUnavailable = new SimpleStringProperty(t);
    }
    
    public String setEndTimeUnavailable(){
        return endTimeUnavailable.get();
    }
    public void setEndTimeUnavailable(String endTimeUnavailable){
        this.endTimeUnavailable = new SimpleStringProperty(endTimeUnavailable);
    }

}
