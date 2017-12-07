package edu.utd.actorDictionary.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.jayway.jsonpath.JsonPath;

import edu.utd.actorDictionary.config.CacheConfig;
import edu.utd.actorDictionary.config.GlobalProperties;
import edu.utd.actorDictionary.domain.Actors;
import edu.utd.actorDictionary.domain.Dictionary;
import edu.utd.actorDictionary.domain.Roles;
import edu.utd.actorDictionary.domain.RolesPK;
import edu.utd.actorDictionary.domain.Synonyms;
import edu.utd.actorDictionary.domain.SynonymsPK;
import edu.utd.actorDictionary.domain.User;
import edu.utd.actorDictionary.dto.RoleDTO;
import edu.utd.actorDictionary.dto.RoleSynonymListJson;
import edu.utd.actorDictionary.dto.SynonymDTO;
import edu.utd.actorDictionary.dto.ValidDate;
import edu.utd.actorDictionary.repository.ActorsRepository;
import edu.utd.actorDictionary.repository.DictionaryRepository;
import edu.utd.actorDictionary.repository.RolesRepository;
import edu.utd.actorDictionary.repository.SynonymsRepository;
import edu.utd.actorDictionary.repository.UserRepository;
import edu.utd.actorDictionary.utility.Utility;

@Service
public class ActorService {

	private static final Logger log = LoggerFactory.getLogger(ActorService.class);
	private GlobalProperties global;

	@Autowired
	private Utility commonUtility;

	@Autowired
	private ActorsRepository actorRepository;

	@Autowired
	private RolesRepository roleRepository;

	@Autowired
	private SynonymsRepository synonymRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private DictionaryRepository dictRepository;

	@Autowired
	public void setGlobal(GlobalProperties global) {
		this.global = global;
	}

	/**
	 * Returns actorlist by username and all actors that dont have username.
	 * basically, returning actors that are not yet edited and edited by given
	 * username
	 * 
	 * @param username
	 * @return
	 */
	public List<String> findAllActors(String username) {

		List<Actors> actorList = actorRepository.findByUsernameOrUsernameIsNull(username);
		List<String> actorNames = new ArrayList<String>();
		for (Actors a : actorList) {
			actorNames.add(a.getName());
		}
		log.info("Actors list size is " + actorList);
		return actorNames;
	}

	/**
	 * Method to register new user. While saving password Md5 hashed salt is
	 * added to the received MD5hashed password and then reversed and saved.
	 * 
	 * @param input
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	@Transactional(rollbackFor = Exception.class)
	public String registerUser(User input) throws NoSuchAlgorithmException {
		if (input != null && !input.getFirstName().isEmpty() && !input.getUsername().isEmpty()
				&& !input.getHashedPassword().isEmpty()) {
			List<User> correctUsers = userRepository.findAll();
			if (correctUsers != null && correctUsers.size() > 0) {
				for (User u : correctUsers) {
					if (u.getUsername().toLowerCase().equals(input.getUsername().toLowerCase())) {
						return "Username is taken";
					}
				}
			}
			input.setHashedPassword(commonUtility.modifyPassword(input.getHashedPassword()));
			userRepository.save(input);
			return "registration Successful";
		}

		return "Username or Password is Missing";
	}

	/**
	 * Method to verify login credentials.
	 * 
	 * @param input
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public String loginUser(User input) throws NoSuchAlgorithmException {
		if (input != null && !input.getUsername().isEmpty() && !input.getHashedPassword().isEmpty()) {
			List<User> correctUsers = userRepository.findByUsername(input.getUsername());
			if (correctUsers != null && correctUsers.size() == 1) {
				User correctUser = correctUsers.get(0);
				if (correctUser.getHashedPassword().equals(commonUtility.modifyPassword(input.getHashedPassword()))
						&& correctUser.getUsername().equals(input.getUsername())) {
					return "Valid User";
				} else {
					return "Invalid Credentials";
				}
			} else {
				return "Invalid Credentials";
			}
		}
		return "UserName or Password Missing";
	}

	/**
	 * Returns list of roles edited by that user or not edited at all.
	 * 
	 * @param username
	 * @return
	 */
	public Map<String, List<RoleDTO>> findRoles(String username) {
		List<Roles> rolesList = roleRepository.findByUsernameOrUsernameIsNull(username);
		log.info("Roles list size is " + rolesList.size());
		Map<String, List<RoleDTO>> map = new HashMap<String, List<RoleDTO>>();
		for (Roles r : rolesList) {
			if (map.containsKey(r.getIndex().getActorName())) {
				List<RoleDTO> list = map.get(r.getIndex().getActorName());
				list.add(new RoleDTO(r.getIndex().getRoleName(), r.getIndex().getActorName(), r.getStartDate(),
						r.getEndDate()));
				map.put(r.getIndex().getActorName(), list);
			} else {
				List<RoleDTO> list = new ArrayList<RoleDTO>();
				list.add(new RoleDTO(r.getIndex().getRoleName(), r.getIndex().getActorName(), r.getStartDate(),
						r.getEndDate()));
				map.put(r.getIndex().getActorName(), list);

			}

		}
		return map;
	}

	/**
	 * Saves roles edited by user and updates the actors,roles,synonyms
	 * accordingly
	 * 
	 * @param input
	 * @param username
	 * @return
	 * @throws ParseException
	 */
	@Transactional(rollbackFor = Exception.class)
	public String saveRole(RoleDTO input, String username) throws ParseException {
		ValidDate validDate = commonUtility.validateDate(input);
		if (input.getKey() != null && input.getKey().length() > 0 && input.getRole() != null
				&& input.getRole().length() > 0 && validDate.isValid()) {
			RolesPK rolespk = new RolesPK(input.getRole(), input.getKey());
			Roles roles = new Roles(rolespk, validDate.getStartDate(), validDate.getEndDate(), username);
			roleRepository.save(roles);
			roleRepository.updateRoles(username, input.getKey());
			actorRepository.updateActors(username, input.getKey());
			synonymRepository.updateSynonyms(username, input.getKey());
			return "Success";

		}
		// TODO Auto-generated method stub
		return "InvalidData";
	}

	/**
	 * Deletes role. Only roles assigned by the given user can be deleted.
	 * @param input
	 * @param username
	 * @return
	 */
	public String deleteRole(RoleDTO input, String username) {

		if (input.getKey() != null && input.getKey().length() > 0 && input.getRole() != null
				&& input.getRole().length() > 0) {
			List<Roles> roles = roleRepository.findByIndexActorNameAndIndexRoleName(input.getKey(), input.getRole());
			if (roles != null && roles.size() > 0) {
				if (roles.get(0).getUsername().equals(username)) {
					RolesPK rolespk = new RolesPK(input.getRole(), input.getKey());
					// Roles roles = new
					// Roles(rolespk,input.getStart(),input.getEnd());
					roleRepository.delete(rolespk);
					return "success";
				}
			}

		}
		return "invalidData";
	}

	/**
	 * Finds all the synonyms of all actors assigned by the username or where username is null 
	 * @param username
	 * @return
	 */
	public Map<String, List<String>> findSynonyms(String username) {
		List<Synonyms> synonyms = synonymRepository.findByUsernameOrUsernameIsNull(username);
		log.info("Synonyms list size is " + synonyms.size());
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		for (Synonyms s : synonyms) {
			if (map.containsKey(s.getIndex().getActorName())) {
				List<String> list = map.get(s.getIndex().getActorName());
				list.add(s.getIndex().getSynonym());
				map.put(s.getIndex().getActorName(), list);
			} else {
				List<String> list = new ArrayList<String>();
				list.add(s.getIndex().getSynonym());
				map.put(s.getIndex().getActorName(), list);

			}
		}
		return map;
	}

	public Map<String, List<RoleDTO>> findRoles() {
		List<Roles> rolesList = roleRepository.findAll();
		log.info("Roles list size is " + rolesList.size());
		Map<String, List<RoleDTO>> map = new HashMap<String, List<RoleDTO>>();
		for (Roles r : rolesList) {
			if (map.containsKey(r.getIndex().getActorName())) {
				List<RoleDTO> list = map.get(r.getIndex().getActorName());
				list.add(new RoleDTO(r.getIndex().getRoleName(), r.getIndex().getActorName(), r.getStartDate(),
						r.getEndDate()));
				map.put(r.getIndex().getActorName(), list);
			} else {
				List<RoleDTO> list = new ArrayList<RoleDTO>();
				list.add(new RoleDTO(r.getIndex().getRoleName(), r.getIndex().getActorName(), r.getStartDate(),
						r.getEndDate()));
				map.put(r.getIndex().getActorName(), list);

			}

		}
		return map;
	}

	public Map<String, List<String>> findSynonyms() {
		List<Synonyms> synonyms = synonymRepository.findAll();
		log.info("Synonyms list size is " + synonyms.size());
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		for (Synonyms s : synonyms) {
			if (map.containsKey(s.getIndex().getActorName())) {
				List<String> list = map.get(s.getIndex().getActorName());
				list.add(s.getIndex().getSynonym());
				map.put(s.getIndex().getActorName(), list);
			} else {
				List<String> list = new ArrayList<String>();
				list.add(s.getIndex().getSynonym());
				map.put(s.getIndex().getActorName(), list);

			}
		}
		return map;
	}

	/**
	 * This method gets top wiki links for actors assigned to username or unassigned actors
	 * @param username
	 * @return
	 */
	public Map<String, Map<String, String>> findActorWikiByUser(String username) {
		List<Actors> actorList = actorRepository.findByUsernameOrUsernameIsNull(username);
		Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();

		for (Actors a : actorList) {
			Map<String, String> innerMap = new HashMap<>();
			try {

				HttpTransport httpTransport = new NetHttpTransport();
				HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
				JSONParser parser = new JSONParser();
				GenericUrl url = new GenericUrl("https://kgsearch.googleapis.com/v1/entities:search");
				url.put("query", a.getName());
				url.put("limit", "3");
				url.put("indent", "true");
				url.put("key", global.getApiKey());
				HttpRequest request = requestFactory.buildGetRequest(url);
				HttpResponse httpResponse = request.execute();
				JSONObject response = (JSONObject) parser.parse(httpResponse.parseAsString());
				JSONArray elements = (JSONArray) response.get("itemListElement");
				for (Object element : elements) {
					System.out.println(JsonPath.read(element, "$.result.detailedDescription.url").toString());
					innerMap.put(a.getName(), JsonPath.read(element, "$.result.detailedDescription.url").toString());
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			map.put(a.getName(), innerMap);
		}

		return map;
	}
	
	/**
	 * This method gets top wiki links for actors assigned to username or unassigned actors
	 * @param username
	 * @return
	 */
	public Map<String, List<String>> findActorWiki(String actor) {
		//List<Actors> actorList = actorRepository.findByUsernameOrUsernameIsNull(username);
		Map<String, List<String>> map = new HashMap<String, List<String>>();

	//	for (Actors a : actorList) {
			List<String> actorWikis= new ArrayList<String>();
			try {

				HttpTransport httpTransport = new NetHttpTransport();
				HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
				JSONParser parser = new JSONParser();
				GenericUrl url = new GenericUrl("https://kgsearch.googleapis.com/v1/entities:search");
				url.put("query", actor);
				url.put("limit", "3");
				url.put("indent", "true");
				url.put("key", global.getApiKey());
				HttpRequest request = requestFactory.buildGetRequest(url);
				HttpResponse httpResponse = request.execute();
				JSONObject response = (JSONObject) parser.parse(httpResponse.parseAsString());
				JSONArray elements = (JSONArray) response.get("itemListElement");
				for (Object element : elements) {
					System.out.println(JsonPath.read(element, "$.result.detailedDescription.url").toString());
					actorWikis.add(JsonPath.read(element, "$.result.detailedDescription.url").toString());
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			map.put(actor, actorWikis);
	//	}

		return map;
	}

	/**
	 * Uploads data to dictionary
	 * @param input
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean saveData(Map<String, RoleSynonymListJson> input) {
		boolean rolesFlag = false, synonymFlag = false;
		if (input != null) {

			for (Map.Entry<String, RoleSynonymListJson> entry : input.entrySet()) {

				if (entry.getValue() != null) {
					if (entry.getKey() != null && entry.getKey().length() > 0) {
						Actors a = new Actors(entry.getKey());
						actorRepository.save(a);
					}

					RoleSynonymListJson data = entry.getValue();
					if (data.getRoles() != null && data.getRoles().size() > 0) {

						List<Roles> roles = new ArrayList<>();

						for (String rol : data.getRoles()) {
							RolesPK rolePk = new RolesPK(rol, entry.getKey());
							Roles r = new Roles(rolePk, "", "");
							roles.add(r);
						}
						if (roles.size() > 0) {

							List<Roles> savedRoles = roleRepository.save(roles);
							if (savedRoles.size() > 0) {
								log.info("Saved " + savedRoles.size() + " roles");
								rolesFlag = true;

							}
						}

					}
					if (data.getSynonyms() != null && data.getSynonyms().size() > 0) {
						List<Synonyms> synonyms = new ArrayList<>();
						for (String s : data.getSynonyms()) {
							SynonymsPK synPk = new SynonymsPK(s, entry.getKey());
							Synonyms synModel = new Synonyms(synPk);
							synonyms.add(synModel);
						}
						if (synonyms.size() > 0) {
							List<Synonyms> savedSynonyms = synonymRepository.save(synonyms);
							if (savedSynonyms.size() > 0) {
								log.info("Saved " + savedSynonyms.size() + " synonyms");
								synonymFlag = true;

							}
						}

					}

				}
			}
		}

		if (synonymFlag || rolesFlag) {
			return true;
		}
		return false;
	}

	/**
	 * Saves synonyms
	 * @param input
	 * @param username
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public String saveSynonym(SynonymDTO input, String username) {
		if (input.getName() != null && input.getName().length() > 0 && input.getSynonym() != null
				&& input.getSynonym().length() > 0) {
			SynonymsPK synPk = new SynonymsPK(input.getSynonym(), input.getName());
			Synonyms s = new Synonyms(synPk,username);
			synonymRepository.updateSynonyms(username, input.getName());
			roleRepository.updateRoles(username, input.getName());
			actorRepository.updateActors(username, input.getName());
			synonymRepository.save(s);
			
			
			return "Success";
		}

		return "InvalidData";
	}

	/**
	 * Deletes Synonyms 
	 * @param input
	 * @param username 
	 * @return
	 */
	public String deleteSynonym(SynonymDTO input, String username) {
		if (input.getName() != null && input.getName().length() > 0 && input.getSynonym() != null
				&& input.getSynonym().length() > 0) {
			List<Synonyms> syns= synonymRepository.findByIndexActorNameAndIndexSynonym(input.getName(), input.getSynonym());
			if(syns!=null && syns.size()>0){
				if(syns.get(0).getUsername().equals(username)){
					SynonymsPK synPk = new SynonymsPK(input.getSynonym(), input.getName());
					// Synonyms s = new Synonyms(synPk);
					synonymRepository.delete(synPk);
					return "Success";
				}
			}
			
		}

		return "InvalidData";
	}

	@Cacheable(CacheConfig.CACHE_ONE)
	public File createFile() throws IOException {
		File file = new File("dictionary.txt");
		PrintWriter printWriter = new PrintWriter(new FileWriter(file));
		List<Actors> actorList = actorRepository.findByUsernameIsNotNull();
		for (Actors actor : actorList) {
			printWriter.println(actor.getName() + "\t#" + actor.getUsername());
			List<Synonyms> synonyms = synonymRepository.findByIndexActorName(actor.getName());
			for (Synonyms syn : synonyms) {
				printWriter.println("+" + syn.getIndex().getSynonym());
			}
			List<Roles> roles = roleRepository.findByIndexActorName(actor.getName());
			for (Roles role : roles) {
				String s = "        [" + role.getIndex().getRoleName();
				if(role.getStartDate()!=null && !role.getStartDate().isEmpty() ){
					s+= " " + role.getStartDate().replace("/", "");
				}
				if(role.getEndDate()!=null && !role.getEndDate().isEmpty())
				 s+= "-"
						+ role.getEndDate().replace("/", "") ;
				s+= "]";
				printWriter.println(s);

			}
		}
		printWriter.close();
		// InputStreamResource resource = new InputStreamResource(new
		// FileInputStream(file));

		return file;
	}

	public List<String> findAllActors() {

		List<Actors> actorList = actorRepository.findByUsernameIsNotNull();
		List<String> actorNames = new ArrayList<String>();
		for (Actors a : actorList) {
			actorNames.add(a.getName());
		}
		log.info("Actors list size is " + actorList);
		return actorNames;
	}

	/**
	 * Save dictionary i.e. actor and link to database.
	 * @param input
	 * @param username
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public String saveDictionary(Dictionary input, String username) {
		if (username != null && username.length() > 0 && input.getTopLink() != null
				&& input.getTopLink().length() > 0) {
			
			input.setUsername(username);
			
			synonymRepository.updateSynonyms(username, input.getActor());
			roleRepository.updateRoles(username, input.getActor());
			actorRepository.updateActors(username, input.getActor());
			dictRepository.save(input);
			
			
			return "Success";
		}

		return "InvalidData";
	}

}
