package edu.utd.actorDictionary.domain;

import java.io.Serializable;

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

	public Synonyms() {
		super();
	}

	public Synonyms(SynonymsPK index) {
		super();
		this.index = index;
	}

	public SynonymsPK getIndex() {
		return index;
	}

	public void setIndex(SynonymsPK index) {
		this.index = index;
	}
	
	
}
