package edu.utd.actorDictionary.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.utd.actorDictionary.domain.Actors;

public interface ActorsRepository  extends JpaRepository<Actors, String>{
	
	
}
