package edu.utd.actorDictionary.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.utd.actorDictionary.domain.Synonyms;
import edu.utd.actorDictionary.domain.SynonymsPK;

public interface SynonymsRepository  extends JpaRepository<Synonyms, SynonymsPK>{

}
