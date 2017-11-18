package edu.utd.actorDictionary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.utd.actorDictionary.domain.Actors;
import edu.utd.actorDictionary.domain.Roles;
import edu.utd.actorDictionary.domain.User;

public interface UserRepository  extends JpaRepository<User, String>{
	
	public List<User> findByUsername(String username);
	
}
