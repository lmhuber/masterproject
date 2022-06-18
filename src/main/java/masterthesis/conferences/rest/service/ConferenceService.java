package masterthesis.conferences.rest.service;

import masterthesis.conferences.data.model.Conference;

import java.util.List;

public interface ConferenceService {

	public List<Conference> findAll();
	
	public Conference findById(int id);
	
	public void save(Conference conferences);
	
	public void deleteById(int id);

	public List<Conference> searchBy(String name);
	
}
