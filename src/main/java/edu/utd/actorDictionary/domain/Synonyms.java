package edu.utd.actorDictionary.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.hateoas.ResourceSupport;

@Entity
@Table(name="Synonyms", schema="UTD")
//@NamedQuery(name="Synonyms.findAll", query="SELECT s FROM Synonyms s")
public class Synonyms extends ResourceSupport implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private SynonymsPK index;

	@Column(name="username")
	private String username;
	
	public Synonyms() {
		super();
	}

	public Synonyms(SynonymsPK index) {
		super();
		this.index = index;
	}

	public Synonyms(SynonymsPK synPk, String username2) {
		super();
		this.index = synPk;	
		this.username=username2;
	}

	public SynonymsPK getIndex() {
		return index;
	}

	public void setIndex(SynonymsPK index) {
		this.index = index;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	
}
