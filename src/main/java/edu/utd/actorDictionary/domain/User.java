package edu.utd.actorDictionary.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.hateoas.ResourceSupport;

@Entity
@Table(name="User", schema="UTD")
public class User extends ResourceSupport implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="username")
	private String username;
	
	@Column(name="password")
	private String hashedPassword;
	
	@Column(name="first_name")
	private String firstName;
	
	@Column(name="last_name")
	private String lastName;
	
	
	public User() {
		super();
	}
	public User(String username, String hasedPassword, String firstName, String lastName) {
		super();
		this.username = username;
		this.hashedPassword = hasedPassword;
		this.firstName = firstName;
		this.lastName = lastName;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getHashedPassword() {
		return hashedPassword;
	}
	public void setHashedPassword(String hashedPassword) {
		this.hashedPassword = hashedPassword;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	
}
