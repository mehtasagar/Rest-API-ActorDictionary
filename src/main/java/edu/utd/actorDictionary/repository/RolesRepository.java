package edu.utd.actorDictionary.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import edu.utd.actorDictionary.domain.Roles;
import edu.utd.actorDictionary.domain.RolesPK;

public interface RolesRepository  extends JpaRepository<Roles, RolesPK>{

	public List<Roles> findByIndexActorName(String actor);
	
	
	
	public List<Roles>  findByUsernameOrUsernameIsNull(String username); 
	
	@Modifying(clearAutomatically = true)
    @Query("UPDATE Roles r SET r.username = :username WHERE r.index.actorName = :actor_name")
    int updateRoles(@Param("username") String username, @Param("actor_name") String actorName);

	public List<Roles> findByIndexActorNameAndIndexRoleName(String key, String role);


}
