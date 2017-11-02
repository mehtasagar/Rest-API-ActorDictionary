package edu.utd.actorDictionary.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.hateoas.ResourceSupport;

@Entity
@Table(name="Actors", schema="UTD")
//@NamedQuery(name="Actors.findAll", query="SELECT a FROM Actors a")
public class Actors extends ResourceSupport implements Serializable {
	
	
	
	public Actors() {
		super();
	}

	public Actors(String name) {
		super();
		this.name = name;
	}

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="Name")
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
