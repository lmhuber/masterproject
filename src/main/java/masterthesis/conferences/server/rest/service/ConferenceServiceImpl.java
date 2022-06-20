package masterthesis.conferences.server.rest.service;

import masterthesis.conferences.data.ConferenceRepository;
import masterthesis.conferences.data.model.Conference;
import masterthesis.conferences.server.rest.storage.StorageController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConferenceServiceImpl implements ConferenceService {

	private ConferenceRepository conferenceRepository;

	@Autowired
	public ConferenceServiceImpl() {
		this.conferenceRepository = StorageController.getRepository();
	}
	
	@Override
	public List<Conference> findAll() {
		return conferenceRepository.getConferences();
	}

	@Override
	public Conference findById(String title) {
		Optional<Conference> result = conferenceRepository.findById(title);
		Conference conference = null;

		if (result.isPresent() && result != null) {
			conference = (Conference) result.get();
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
	public List<Conference> searchBy(String name) {
		return List.of(conferenceRepository.getConference(name));
	}

}






