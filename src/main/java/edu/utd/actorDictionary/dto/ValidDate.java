package edu.utd.actorDictionary.dto;

public class ValidDate {
	
	private String startDate;
	private String endDate;
	private boolean valid;
	
	public ValidDate() {
		super();
	}
	
	public ValidDate(String startDate, String endDate, boolean valid) {
		super();
		this.startDate = startDate;
		this.endDate = endDate;
		this.valid = valid;
	}
	
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	
	
	
	
	
}
