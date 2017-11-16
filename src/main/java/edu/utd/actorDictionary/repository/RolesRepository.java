package edu.utd.actorDictionary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.utd.actorDictionary.domain.Roles;
import edu.utd.actorDictionary.domain.RolesPK;

public interface RolesRepository  extends JpaRepository<Roles, RolesPK>{

	public List<Roles> findByIndexActorName(String actor);

}
