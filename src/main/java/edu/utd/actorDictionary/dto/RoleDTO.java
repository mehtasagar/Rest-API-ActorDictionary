package edu.utd.actorDictionary.dto;

public class RoleDTO {
	
		
	private String role;
	private String key;
	private String start;
	private String end;
	
	
	
	
	public RoleDTO() {
		super();
	}
	public RoleDTO(String role, String key, String start, String end) {
		super();
		this.role = role;
		this.key = key;
		this.start = start;
		this.end = end;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	
	
	
}
