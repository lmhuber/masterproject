package masterthesis.conferences.server.rest.service;

import masterthesis.conferences.data.ConferenceRepository;
import masterthesis.conferences.data.model.Conference;
import masterthesis.conferences.data.model.ConferenceEdition;
import masterthesis.conferences.server.controller.ServerController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConferenceServiceImpl implements ConferenceService {

	private final ConferenceRepository conferenceRepository;

	@Autowired
	public ConferenceServiceImpl() {
		this.conferenceRepository = ServerController.getRepository();
	}
	
	@Override
	public List<Conference> findAll() {
		return conferenceRepository.getConferences();
	}

	@Override
	public Conference findById(String title) {
		Optional<Conference> result = conferenceRepository.findById(title);
		Conference conference;

		if (result.isPresent()) {
			conference = result.get();
		} else {
			throw new RuntimeException("Did not find conference id - " + title);
		}

		return conference;
	}

	@Override
	public void save(Conference conference) {
		try {
			conferenceRepository.save(conference);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void deleteById(String title) {
		try {
			conferenceRepository.deleteById(title);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public ConferenceEdition findById(int id) {
		return conferenceRepository.getEdition(id);
	}

	@Override
	public void save(ConferenceEdition edition, String title) {
		try {
			conferenceRepository.addEdition(findById(title), edition);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<Conference> searchBy(String name) {
		return List.of(conferenceRepository.getConference(name));
	}

}






