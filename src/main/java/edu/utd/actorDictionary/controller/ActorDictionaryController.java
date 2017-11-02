package edu.utd.actorDictionary.controller;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import edu.utd.actorDictionary.domain.Synonyms;
import edu.utd.actorDictionary.dto.RoleDTO;
import edu.utd.actorDictionary.dto.RoleSynonymListJson;
import edu.utd.actorDictionary.dto.SynonymDTO;
import edu.utd.actorDictionary.exceptions.UnauthorizedAccessException;
import edu.utd.actorDictionary.service.ActorService;

@RestController
public class ActorDictionaryController {

	private static final Logger log = LoggerFactory.getLogger(ActorDictionaryController.class);

	@Autowired
	private ActorService service;

	/*
	 * @ExceptionHandler(UnauthorizedAccess.class) public ResponseEntity<Object>
	 * forbidden() { UnauthorizedError error = new UnauthorizedError(
	 * "Access Denied"); return new ResponseEntity<Object>(error,
	 * HttpStatus.UNAUTHORIZED); }
	 */

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/dictionary/actors", headers = { "Accept=application/json" })
	public List<String> getActors(@RequestHeader(value = "secret-key") String secretKey) throws ParseException {
		boolean valid = validate(secretKey);
		if (valid) {
			log.info("Client request :: actors ");
			List<String> actorList = service.findAllActors();

			log.info("Returning " + actorList.size() + " rows. ");
			return actorList;
		} else {
			throw new UnauthorizedAccessException();
		}

	}

	private boolean validate(String secretKey) {
		if (secretKey.equals("mySecretKey")) {
			return true;
		}
		return false;
	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/dictionary/actorRoles", headers = {
			"Accept=application/json" })
	public Map<String, List<RoleDTO>> getRoles(@RequestHeader(value = "secret-key") String secretKey)
			throws ParseException {
		boolean valid = validate(secretKey);
		if (valid) {
			log.info("Client request :: roles ");
			Map<String, List<RoleDTO>> roleMap = service.findRoles();

			log.info("Returning " + roleMap.size() + " rows. ");
			return roleMap;
		} else {
			throw new UnauthorizedAccessException();
		}

	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/dictionary/synonyms", headers = { "Accept=application/json" })
	public Map<String, List<String>> getSynonyms(@RequestHeader(value = "secret-key") String secretKey)
			throws ParseException {
		boolean valid = validate(secretKey);
		if (valid) {
			log.info("Client request :: synonyms ");
			Map<String, List<String>> synonyms = service.findSynonyms();

			log.info("Returning " + synonyms.size() + " rows. ");
			return synonyms;
		} else {
			throw new UnauthorizedAccessException();
		}

	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/dictionary/wikiForActors", headers = {
			"Accept=application/json" })
	public Map<String, Map<String, String>> getWikiForActors(@RequestHeader(value = "secret-key") String secretKey)
			throws ParseException {
		boolean valid = validate(secretKey);
		if (valid) {
			log.info("Client request :: Wike For Actors ");
			Map<String, Map<String, String>> actorWiki = service.findActorWiki();

			log.info("Returning " + actorWiki.size() + " rows. ");
			return actorWiki;
		} else {
			throw new UnauthorizedAccessException();
		}

	}
	
	@CrossOrigin
	@RequestMapping(method = RequestMethod.POST, value = "/dictionary/upload", headers = {
			"Accept=application/json" })
	public ResponseEntity<?> uploadDictionaryData(@RequestHeader(value = "secret-key") String secretKey,@RequestBody Map<String,RoleSynonymListJson> input)
			throws ParseException {
		boolean valid = validate(secretKey);
		if (valid) {
			log.info("Client request :: upload data ");
			boolean success = service.saveData(input);
			if(success){
				return new ResponseEntity<>(HttpStatus.OK);
			}
			
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			throw new UnauthorizedAccessException();
		}

	}
	
	@CrossOrigin
	@RequestMapping(method = RequestMethod.POST, value = "/dictionary/saveSynonym", headers = {
			"Accept=application/json" })
	public ResponseEntity<?> addSynonym(@RequestHeader(value = "secret-key") String secretKey,@RequestBody SynonymDTO input)
			throws ParseException {
		boolean valid = validate(secretKey);
		if (valid) {
			log.info("Client request :: upload data ");
			boolean success = service.saveSynonym(input);
			if(success){
				return new ResponseEntity<>(HttpStatus.OK);
			}
			
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			throw new UnauthorizedAccessException();
		}

	}
	
	@CrossOrigin
	@RequestMapping(method = RequestMethod.DELETE, value = "/dictionary/deleteSynonym", headers = {
			"Accept=application/json" })
	public ResponseEntity<?> deleteSynonym(@RequestHeader(value = "secret-key") String secretKey,@RequestBody SynonymDTO input)
			throws ParseException {
		boolean valid = validate(secretKey);
		if (valid) {
			log.info("Client request :: upload data ");
			boolean success = service.deleteSynonym(input);
			if(success){
				return new ResponseEntity<>(HttpStatus.OK);
			}
			
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			throw new UnauthorizedAccessException();
		}

	}
	
	
	@CrossOrigin
	@RequestMapping(method = RequestMethod.POST, value = "/dictionary/saveRole", headers = {
			"Accept=application/json" })
	public ResponseEntity<?> addRole(@RequestHeader(value = "secret-key") String secretKey,@RequestBody RoleDTO input)
			throws ParseException {
		boolean valid = validate(secretKey);
		if (valid) {
			log.info("Client request :: upload data ");
			boolean success = service.saveRole(input);
			if(success){
				return new ResponseEntity<>(HttpStatus.OK);
			}
			
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			throw new UnauthorizedAccessException();
		}

	}
	
	@CrossOrigin
	@RequestMapping(method = RequestMethod.DELETE, value = "/dictionary/deleteRole", headers = {
			"Accept=application/json" })
	public ResponseEntity<?> deleteRole(@RequestHeader(value = "secret-key") String secretKey,@RequestBody RoleDTO input)
			throws ParseException {
		boolean valid = validate(secretKey);
		if (valid) {
			log.info("Client request :: upload data ");
			boolean success = service.deleteRole(input);
			if(success){
				return new ResponseEntity<>(HttpStatus.OK);
			}
			
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			throw new UnauthorizedAccessException();
		}

	}


}
