package masterthesis.conferences.rest.service;

import masterthesis.conferences.data.ConferenceRepository;
import masterthesis.conferences.data.model.Conference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConferenceServiceImpl implements ConferenceService {

	private ConferenceRepository conferenceRepository;

	@Autowired
	public ConferenceServiceImpl(ConferenceRepository conferenceRepository) {
		conferenceRepository = conferenceRepository;
	}
	
	@Override
	public List<Conference> findAll() {

		//return conferenceRepository.getConferences();
		return null;
	}

	@Override
	public Conference findById(int id) {
		//Optional<Conference> result = conferenceRepository.findById(id);
		Optional result = null;
		Conference conference = null;
		
		if (result.isPresent()) {
			conference = (Conference) result.get();
		}
		else {
			throw new RuntimeException("Did not find conference id - " + id);
		}
		
		return conference;
	}

	@Override
	public void save(Conference conference) {

		//conferenceRepository.save(conference);
	}

	@Override
	public void deleteById(int id) {
		//conferenceRepository.deleteById(id);
	}

	@Override
	public List<Conference> searchBy(String name) {
		
		//return conferenceRepository.getConference(name);
		return null;
	}

}






