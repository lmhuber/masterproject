package masterthesis.conferences.server.rest.service;

import masterthesis.conferences.data.model.Conference;

import java.util.List;

public interface ConferenceService {

	public List<Conference> findAll();

	//TODO: search for title (key for conference is title)
	public Conference findById(String title);

	public void save(Conference conferences);

	public void deleteById(String title);

	public List<Conference> searchBy(String name);

}
