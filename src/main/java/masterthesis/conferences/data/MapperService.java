package masterthesis.conferences.data;

import masterthesis.conferences.data.dto.ConferenceDTO;
import masterthesis.conferences.data.model.Conference;

public class MapperService {
    private ConferenceRepository repository;

    public MapperService(ConferenceRepository repository) {
        this.repository = repository;
    }

    public ConferenceDTO convertToConferenceDTO(String title) {
        Conference conference = repository.getConference(title);
        return new ConferenceDTO(conference.getTitle(), conference.getOrganization(),
                conference.getPublisher(), conference.getConferenceEditionIds());
    }

}
