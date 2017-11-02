package edu.utd.actorDictionary.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.jayway.jsonpath.JsonPath;

import edu.utd.actorDictionary.config.GlobalProperties;
import edu.utd.actorDictionary.domain.Actors;
import edu.utd.actorDictionary.domain.Roles;
import edu.utd.actorDictionary.domain.RolesPK;
import edu.utd.actorDictionary.domain.Synonyms;
import edu.utd.actorDictionary.domain.SynonymsPK;
import edu.utd.actorDictionary.dto.RoleDTO;
import edu.utd.actorDictionary.dto.RoleSynonymListJson;
import edu.utd.actorDictionary.dto.SynonymDTO;
import edu.utd.actorDictionary.repository.ActorsRepository;
import edu.utd.actorDictionary.repository.RolesRepository;
import edu.utd.actorDictionary.repository.SynonymsRepository;

@Service
public class ActorService {

	private static final Logger log = LoggerFactory.getLogger(ActorService.class);
	private GlobalProperties global;

	
	@Autowired
	private ActorsRepository actorRepository;

	@Autowired
	private RolesRepository roleRepository;

	@Autowired
	private SynonymsRepository synonymRepository;

	@Autowired
    public void setGlobal(GlobalProperties global) {
        this.global = global;
    }
	
	public List<String> findAllActors() {

		List<Actors> actorList = actorRepository.findAll();
		List<String> actorNames = new ArrayList<String>();
		for (Actors a : actorList) {
			actorNames.add(a.getName());
		}
		log.info("Actors list size is " + actorList);
		return actorNames;
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
			// map.put(r.getIndex().getActorName(), new
			// RoleDTO(r.getIndex().getRoleName(),
			// r.getIndex().getActorName(),r.getStartDate(), r.getEndDate()));
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

	public Map<String, Map<String, String>> findActorWiki() {
		List<Actors> actorList = actorRepository.findAll();
		Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();

		for (Actors a : actorList) {
			Map<String, String> innerMap = new HashMap<>();
			try {
				// properties.load(new FileInputStream("kgsearch.properties"));

				HttpTransport httpTransport = new NetHttpTransport();
				HttpRequestFactory requestFactory = httpTransport.createRequestFactory();
				JSONParser parser = new JSONParser();
				GenericUrl url = new GenericUrl("https://kgsearch.googleapis.com/v1/entities:search");
				url.put("query", a.getName());
				url.put("limit", "1");
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

	@Transactional(rollbackFor = Exception.class)
	public boolean saveData(Map<String, RoleSynonymListJson> input) {
		boolean rolesFlag = false, synonymFlag = false;
		if (input != null) {

			for (Map.Entry<String, RoleSynonymListJson> entry : input.entrySet()) {

				if (entry.getValue() != null) {
					if(entry.getKey()!=null && entry.getKey().length()>0){
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
								// RoleSynonymListJson data =
								// entry.getValue();

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

		if (synonymFlag && rolesFlag) {
			return true;
		}
		return false;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public boolean saveSynonym(SynonymDTO input) {
		if(input.getName()!=null && input.getName().length()>0 && input.getSynonym()!=null && input.getSynonym().length()>0){
			SynonymsPK synPk=new SynonymsPK( input.getSynonym(),input.getName());
			Synonyms s = new Synonyms(synPk);
			synonymRepository.save(s);
			return true;
		}

		return false;
	}

	public boolean deleteSynonym(SynonymDTO input) {
		if(input.getName()!=null && input.getName().length()>0 && input.getSynonym()!=null && input.getSynonym().length()>0){
			
			
			SynonymsPK synPk=new SynonymsPK( input.getSynonym(),input.getName());
			//Synonyms s = new Synonyms(synPk);
			synonymRepository.delete(synPk);
			return true;
		}

		return false;
	}

	public boolean deleteRole(RoleDTO input) {
		if(input.getKey()!=null && input.getKey().length()>0 && input.getRole()!=null && input.getRole().length()>0){
			RolesPK rolespk= new RolesPK(input.getRole(),input.getKey());
			//Roles roles = new Roles(rolespk,input.getStart(),input.getEnd());
			roleRepository.delete(rolespk);
			return true;
			
		}
		return false;
	}

	@Transactional(rollbackFor = Exception.class)
	public boolean saveRole(RoleDTO input) {
		if(input.getKey()!=null && input.getKey().length()>0 && input.getRole()!=null && input.getRole().length()>0){
			RolesPK rolespk= new RolesPK(input.getRole(),input.getKey());
			Roles roles = new Roles(rolespk,input.getStart(),input.getEnd());
			roleRepository.save(roles);
			return true;
			
		}
		// TODO Auto-generated method stub
		return false;
	}

}
