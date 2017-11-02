package edu.utd.actorDictionary.domain;

import java.io.Serializable;

import javax.persistence.*;

@Embeddable
public class RolesPK implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Column(name="Role_Name")
	private String roleName;
	
	@Column(name="Actor_Name")
	private String actorName;

	public RolesPK() {
		super();
	}

	public RolesPK(String roleName, String actorName) {
		super();
		this.roleName = roleName;
		this.actorName = actorName;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getActorName() {
		return actorName;
	}

	public void setActorName(String actorName) {
		this.actorName = actorName;
	}

	
	
	
	
}
