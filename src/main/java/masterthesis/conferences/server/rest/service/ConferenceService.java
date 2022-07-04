package masterthesis.conferences.server.rest.service;

import masterthesis.conferences.data.model.Conference;
import masterthesis.conferences.data.model.ConferenceEdition;

import java.util.List;

public interface ConferenceService {

	List<Conference> findAll();

	Conference findById(String title);

	ConferenceEdition findById(int id);

	void save(ConferenceEdition edition, String title);

	void save(Conference conferences);

	void deleteById(String title);

	List<Conference> searchBy(String name);

}
