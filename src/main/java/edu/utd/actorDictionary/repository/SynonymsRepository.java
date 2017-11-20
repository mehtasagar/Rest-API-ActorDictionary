package edu.utd.actorDictionary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import edu.utd.actorDictionary.domain.Roles;
import edu.utd.actorDictionary.domain.Synonyms;
import edu.utd.actorDictionary.domain.SynonymsPK;

public interface SynonymsRepository  extends JpaRepository<Synonyms, SynonymsPK>{

	public List<Synonyms> findByIndexActorName(String actorName);
	
	public List<Synonyms> findByUsernameOrUsernameIsNull(String username);
	
	@Modifying(clearAutomatically = true)
    @Query("UPDATE Synonyms s SET s.username = :username WHERE s.index.actorName = :actor_name")
    int updateSynonyms(@Param("username") String username, @Param("actor_name") String actorName);
	
	public List<Synonyms> findByIndexActorNameAndIndexSynonym(String key, String synonym);

	
}
