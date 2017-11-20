package edu.utd.actorDictionary.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.hateoas.ResourceSupport;

@Entity
@Table(name="Roles", schema="UTD")
//@NamedQuery(name="Roles.findAll", query="SELECT r FROM Roles r")
public class Roles extends ResourceSupport implements Serializable{
	
	private static final long serialVersionUID = 1L;

	public Roles() {
		super();
	}

	public Roles(RolesPK index, String startDate, String endDate) {
		super();
		this.index = index;
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	public Roles(RolesPK index, String startDate, String endDate,String username) {
		super();
		this.index = index;
		this.startDate = startDate;
		this.endDate = endDate;
		this.username= username;
	}


	@EmbeddedId
	private RolesPK index;


	@Column(name="Start_date")
	private String startDate;
	
	@Column(name="End_date")
	private String endDate;
	
	@Column(name="username")
	private String username;

	
	
	public RolesPK getIndex() {
		return index;
	}

	public void setIndex(RolesPK index) {
		this.index = index;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	

	
	
	
}
