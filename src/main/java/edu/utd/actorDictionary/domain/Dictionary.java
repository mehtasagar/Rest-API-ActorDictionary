package edu.utd.actorDictionary.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.hateoas.ResourceSupport;

@Entity
@Table(name="Dictionary", schema="UTD")
public class Dictionary extends ResourceSupport implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="actor")
	private String actor;
	
	@Column(name="topLink")
	private String topLink;
	
	@Column(name="username")
	private String username;
	
	public Dictionary() {
		super();
	}

	public Dictionary(String actor, String topLink) {
		super();
		this.actor = actor;
		this.topLink = topLink;
	}

	public String getActor() {
		return actor;
	}

	public void setActor(String actor) {
		this.actor = actor;
	}

	public String getTopLink() {
		return topLink;
	}

	public void setTopLink(String topLink) {
		this.topLink = topLink;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	
	
}
