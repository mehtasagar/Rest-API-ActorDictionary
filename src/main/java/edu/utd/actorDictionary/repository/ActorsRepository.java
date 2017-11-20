package edu.utd.actorDictionary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import edu.utd.actorDictionary.domain.Actors;

public interface ActorsRepository  extends JpaRepository<Actors, String>{
	
	public List<Actors>  findByUsernameOrUsernameIsNull(String username); 
	
	public List<Actors>  findByUsernameIsNotNull(); 

	
	@Modifying(clearAutomatically = true)
    @Query("UPDATE Actors a SET a.username = :username WHERE a.name = :actor_name")
    int updateActors(@Param("username") String username, @Param("actor_name") String actorName);
}
