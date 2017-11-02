package edu.utd.actorDictionary.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class SynonymsPK implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Column(name="Synonym")
	private String synonym;
	
	@Column(name="Actor_Name")
	private String actorName;



	public SynonymsPK() {
		super();
	}

	public SynonymsPK(String synonym, String actorName) {
		super();
		this.synonym = synonym;
		this.actorName = actorName;
	}

	public String getSynonym() {
		return synonym;
	}

	public void setSynonym(String synonym) {
		this.synonym = synonym;
	}

	public String getActorName() {
		return actorName;
	}

	public void setActorName(String actorName) {
		this.actorName = actorName;
	}

	
	
	
	
}
