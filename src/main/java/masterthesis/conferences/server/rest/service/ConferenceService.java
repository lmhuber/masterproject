package masterthesis.conferences.server.rest.service;

import masterthesis.conferences.data.model.Conference;
import masterthesis.conferences.data.model.ConferenceEdition;

import java.util.List;

public interface ConferenceService {

	public List<Conference> findAll();

	//TODO: search for title (key for conference is title)
	public Conference findById(String title);

	public ConferenceEdition findById(int id);

	public void save(ConferenceEdition edition, String title);

	public void save(Conference conferences);

	public void deleteById(String title);

	public List<Conference> searchBy(String name);

}
