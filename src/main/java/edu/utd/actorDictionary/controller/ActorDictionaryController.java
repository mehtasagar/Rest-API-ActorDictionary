package edu.utd.actorDictionary.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import edu.utd.actorDictionary.config.GlobalProperties;
import edu.utd.actorDictionary.domain.Dictionary;
import edu.utd.actorDictionary.domain.User;
import edu.utd.actorDictionary.dto.RoleDTO;
import edu.utd.actorDictionary.dto.RoleSynonymListJson;
import edu.utd.actorDictionary.dto.SynonymDTO;
import edu.utd.actorDictionary.exceptions.UnauthorizedAccessException;
import edu.utd.actorDictionary.service.ActorService;
import edu.utd.actorDictionary.utility.Utility;

@RestController
public class ActorDictionaryController {

	private static final Logger log = LoggerFactory.getLogger(ActorDictionaryController.class);
	private GlobalProperties global;

	@Autowired
	private ActorService service;

	@Autowired
	private Utility commonService;

	@Autowired
	public void setGlobal(GlobalProperties global) {
		this.global = global;
	}

	/**
	 * This is API endpoint for getting list of actors.
	 * 
	 * @param secretKey
	 * @return
	 * @throws ParseException
	 */
	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/dictionary/actors/{username}", headers = { "Accept=application/json" })
	public List<String> getActors(@RequestHeader(value = "secret-key") String secretKey,@PathVariable String username) throws ParseException {
		boolean valid = commonService.validate(secretKey);
		if (valid) {
			log.info("Client request :: actors ");
			List<String> actorList = service.findAllActors(username);

			log.info("Returning " + actorList.size() + " rows. ");
			return actorList;
		} else {
			throw new UnauthorizedAccessException();
		}

	}

	/**
	 * This function validates the request
	 * 
	 * @param secretKey
	 * @return
	 *//*
		 * private boolean validate(String secretKey) { if
		 * (secretKey.equals(global.getSecretKey())) { return true; } return
		 * false; }
		 */
	/**
	 * This is API endpoint for getting list of roles.
	 * 
	 * @param secretKey
	 * @return
	 * @throws ParseException
	 */
	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/dictionary/actorRoles/{username}", headers = {
			"Accept=application/json" })
	public Map<String, List<RoleDTO>> getRoles(@RequestHeader(value = "secret-key") String secretKey,@PathVariable String username)
			throws ParseException {
		boolean valid = commonService.validate(secretKey);
		if (valid) {
			log.info("Client request :: roles ");
			Map<String, List<RoleDTO>> roleMap = service.findRoles(username);

			log.info("Returning " + roleMap.size() + " rows. ");
			return roleMap;
		} else {
			throw new UnauthorizedAccessException();
		}

	}

	/**
	 * This is API endpoint for getting list of synonyms.
	 * 
	 * @param secretKey
	 * @return
	 * @throws ParseException
	 */
	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/dictionary/synonyms/{username}", headers = { "Accept=application/json" })
	public Map<String, List<String>> getSynonyms(@RequestHeader(value = "secret-key") String secretKey,@PathVariable String username)
			throws ParseException {
		boolean valid = commonService.validate(secretKey);
		if (valid) {
			log.info("Client request :: synonyms ");
			Map<String, List<String>> synonyms = service.findSynonyms(username);

			log.info("Returning " + synonyms.size() + " rows. ");
			return synonyms;
		} else {
			throw new UnauthorizedAccessException();
		}

	}

	/**
	 * This is API endpoint for getting wiki links for actors by username
	 * 
	 * @param secretKey
	 * @return
	 * @throws ParseException
	 */
	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/dictionary/wikiForActors/{username}", headers = {
			"Accept=application/json" })
	public Map<String, Map<String, String>> getWikiForActorsByUser(@RequestHeader(value = "secret-key") String secretKey,@PathVariable String username)
			throws ParseException {
		boolean valid = commonService.validate(secretKey);
		if (valid) {
			log.info("Client request :: Wike For Actors ");
			Map<String, Map<String, String>> actorWiki = service.findActorWikiByUser(username);

			log.info("Returning " + actorWiki.size() + " rows. ");
			return actorWiki;
		} else {
			throw new UnauthorizedAccessException();
		}

	}

	/**
	 * This is API endpoint for getting wiki links for actors. Version 2
	 * 
	 * @param secretKey
	 * @return
	 * @throws ParseException
	 */
	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/dictionary/wikiForActorsV2/{actor}", headers = {
			"Accept=application/json" })
	public Map<String, List<String>> getWikiForActors(@RequestHeader(value = "secret-key") String secretKey,@PathVariable String actor)
			throws ParseException {
		boolean valid = commonService.validate(secretKey);
		if (valid) {
			log.info("Client request :: Wike For Actors ");
			Map<String, List< String>> actorWiki = service.findActorWiki(actor);

			log.info("Returning " + actorWiki.size() + " rows. ");
			return actorWiki;
		} else {
			throw new UnauthorizedAccessException();
		}

	}
	
	/**
	 * This is the API endpoint for adding url to dictionary
	 * 
	 * @param secretKey
	 * @param input
	 * @return
	 * @throws ParseException
	 */
	@CrossOrigin
	@RequestMapping(method = RequestMethod.POST, value = "/dictionary/saveDictionary/{username}", headers = {
			"Accept=application/json" })
	public ResponseEntity<?> addDictionary(@RequestHeader(value = "secret-key") String secretKey,
			@RequestBody Dictionary input,@PathVariable String username) throws ParseException {
		boolean valid = commonService.validate(secretKey);
		if (valid) {
			log.info("Client request :: Save Dictionary ");
			String success = service.saveDictionary(input,username);
			if (!success.isEmpty()) {
				log.info("Client request :: Save Dictionary Completed");
				return new ResponseEntity<>(success,HttpStatus.OK);
			}

			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			throw new UnauthorizedAccessException();
		}

	}
	
	
	/**
	 * This is the API endpoint for uploading data for dictionary
	 * 
	 * @param secretKey
	 * @param input
	 * @return
	 * @throws ParseException
	 */
	@CrossOrigin
	@RequestMapping(method = RequestMethod.POST, value = "/dictionary/upload", headers = { "Accept=application/json" })
	public ResponseEntity<?> uploadDictionaryData(@RequestHeader(value = "secret-key") String secretKey,
			@RequestBody Map<String, RoleSynonymListJson> input) throws ParseException {
		boolean valid = commonService.validate(secretKey);
		if (valid) {
			log.info("Client request :: upload data ");
			boolean success = service.saveData(input);
			if (success) {
				return new ResponseEntity<>(HttpStatus.OK);
			}

			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			throw new UnauthorizedAccessException();
		}

	}

	/**
	 * This is the API endpoint for adding synonym
	 * 
	 * @param secretKey
	 * @param input
	 * @return
	 * @throws ParseException
	 */
	@CrossOrigin
	@RequestMapping(method = RequestMethod.POST, value = "/dictionary/saveSynonym/{username}", headers = {
			"Accept=application/json" })
	public ResponseEntity<?> addSynonym(@RequestHeader(value = "secret-key") String secretKey,
			@RequestBody SynonymDTO input,@PathVariable String username) throws ParseException {
		boolean valid = commonService.validate(secretKey);
		if (valid) {
			log.info("Client request :: Save Synonym ");
			String success = service.saveSynonym(input,username);
			if (!success.isEmpty()) {
			
				return new ResponseEntity<>(success,HttpStatus.OK);
			}

			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			throw new UnauthorizedAccessException();
		}

	}

	/**
	 * This is the API endpoint for deleting synonym
	 * 
	 * @param secretKey
	 * @param input
	 * @return
	 * @throws ParseException
	 */
	@CrossOrigin
	@RequestMapping(method = RequestMethod.DELETE, value = "/dictionary/deleteSynonym/{username}", headers = {
			"Accept=application/json" })
	public ResponseEntity<?> deleteSynonym(@RequestHeader(value = "secret-key") String secretKey,
			@RequestBody SynonymDTO input,@PathVariable String username) throws ParseException {
		boolean valid = commonService.validate(secretKey);
		if (valid) {
			log.info("Client request :: Delete Synonyms ");
			String success = service.deleteSynonym(input,username);
			if (!success.isEmpty()) {
				return new ResponseEntity<>(success,HttpStatus.OK);
			}

			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			throw new UnauthorizedAccessException();
		}

	}

	/**
	 * This is the API endpoint for saving the role.
	 * 
	 * @param secretKey
	 * @param input
	 * @return
	 * @throws ParseException
	 */
	@CrossOrigin
	@RequestMapping(method = RequestMethod.POST, value = "/dictionary/saveRole/{username}", headers = {
			"Accept=application/json" })
	public ResponseEntity<?> addRole(@RequestHeader(value = "secret-key") String secretKey, @RequestBody RoleDTO input,@PathVariable String username)
			throws ParseException {
		boolean valid = commonService.validate(secretKey);
		if (valid) {
			log.info("Client request :: upload data ");
			String success = service.saveRole(input,username);
			if (!success.isEmpty()) {
				return new ResponseEntity<>(success,HttpStatus.OK);
			}

			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			throw new UnauthorizedAccessException();
		}

	}

	/**
	 * This is the API endpoint for deleting roles
	 * 
	 * @param secretKey
	 * @param input
	 * @return
	 * @throws ParseException
	 */
	@CrossOrigin
	@RequestMapping(method = RequestMethod.DELETE, value = "/dictionary/deleteRole/{username}", headers = {
			"Accept=application/json" })
	public ResponseEntity<?> deleteRole(@RequestHeader(value = "secret-key") String secretKey,
			@RequestBody RoleDTO input,@PathVariable String username) throws ParseException {
		boolean valid = commonService.validate(secretKey);
		if (valid) {
			log.info("Client request :: upload data ");
			String success = service.deleteRole(input,username);
			if (!success.isEmpty()) {
				return new ResponseEntity<>(success,HttpStatus.OK);
			}

			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			throw new UnauthorizedAccessException();
		}

	}

	@CrossOrigin
	@RequestMapping(method = RequestMethod.GET, value = "/dictionary/downloadDictionary", headers = {
			"Accept=application/json" })
	public ResponseEntity<?> downloadDictionary(@RequestHeader(value = "secret-key") String secretKey)
			// public ResponseEntity<?> downloadDictionary ()
			throws ParseException, IOException {
		boolean valid = commonService.validate(secretKey); // boolean valid =
															// true;

		if (valid) {
			log.info("Client request :: download data ");
			// boolean success = service.saveData(input);

			// File file = new File("README.md");
			File file = service.createFile();
			InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
			// InputStreamResource fileResource = service.createFile();

			boolean success = true;
			if (success) {
				HttpHeaders responseHeaders = new HttpHeaders();
				responseHeaders.add(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
						"attachment; filename=" + file.getName());

				responseHeaders.add(org.springframework.http.HttpHeaders.CONTENT_TYPE, "application/octet-stream");
				// return new Response
				return new ResponseEntity<>(resource, responseHeaders, HttpStatus.OK);
			}

			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			throw new UnauthorizedAccessException();
		}

	}

	/**
	 * This is the API endpoint for saving the role.
	 * 
	 * @param secretKey
	 * @param input
	 * @return
	 * @throws ParseException
	 * @throws NoSuchAlgorithmException 
	 */
	@CrossOrigin
	@RequestMapping(method = RequestMethod.POST, value = "/dictionary/registerUser", headers = {
			"Accept=application/json" })
	public ResponseEntity<?> registerUser(@RequestHeader(value = "secret-key") String secretKey,
			@RequestBody User input) throws ParseException, NoSuchAlgorithmException {
		boolean valid = commonService.validate(secretKey);
		if (valid) {
			log.info("Client request :: Register User ");
			String success = service.registerUser(input);
			if (!success.isEmpty()) {
				log.info("User Registered Succesfully");
				return new ResponseEntity<>(success,HttpStatus.OK);
			}

			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			throw new UnauthorizedAccessException();
		}

	}
	
	/**
	 * This is the API endpoint for saving the role.
	 * 
	 * @param secretKey
	 * @param input
	 * @return
	 * @throws ParseException
	 * @throws NoSuchAlgorithmException 
	 */
	@CrossOrigin
	@RequestMapping(method = RequestMethod.POST, value = "/dictionary/loginUser", headers = {
			"Accept=application/json" })
	public ResponseEntity<?> loginUser(@RequestHeader(value = "secret-key") String secretKey,
			@RequestBody User input) throws ParseException, NoSuchAlgorithmException {
		boolean valid = commonService.validate(secretKey);
		if (valid) {
			log.info("Client request :: Login User ");
			String success = service.loginUser(input);
			if (!success.isEmpty()) {
				log.info("valid user");
				return new ResponseEntity<>(success,HttpStatus.OK);
			}
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			throw new UnauthorizedAccessException();
		}

	}

	
	
	
	
}
