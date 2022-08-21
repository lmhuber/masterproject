package masterthesis.conferences.server.controller.storage;

import masterthesis.conferences.data.model.Conference;
import masterthesis.conferences.data.model.dto.AdditionalMetricDTO;
import masterthesis.conferences.data.model.dto.ConferenceDTO;
import masterthesis.conferences.data.model.dto.ConferenceEditionDTO;
import masterthesis.conferences.data.model.dto.IngestConfigurationDTO;
import masterthesis.conferences.server.rest.service.ConferenceService;
import masterthesis.conferences.server.rest.service.ConferenceServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class MapperService {
    @Autowired
    private final ConferenceService conferenceService = new ConferenceServiceImpl();

    public MapperService() {
    }

    public ConferenceDTO convertToConferenceDTO(String title) {
        return new ConferenceDTO(conferenceService.findById(title));
    }

    public ConferenceEditionDTO convertToConferenceEditionDTO(int id) {
        return new ConferenceEditionDTO(id, conferenceService.findById(id));
    }

    public AdditionalMetricDTO convertToAdditionalMetricDTO(int id) {
        try {
            return new AdditionalMetricDTO(conferenceService.findByMetricId(id),
                    conferenceService.findEditionByMetricId(id).getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public IngestConfigurationDTO convertToIngestConfigurationDTO(int id) {
        if (conferenceService.findConfigById(id) == null) return null;
        return new IngestConfigurationDTO(conferenceService.findConfigById(id));
    }

    public Conference convertToConference(ConferenceDTO conferenceDTO) throws ExecutionException, InterruptedException {
        if (conferenceDTO == null) return null;
        Conference conference = new Conference(conferenceDTO.getTitle(),
                conferenceDTO.getOrganization(),
                conferenceDTO.getPublisher());
        for (int i : conferenceDTO.getConferenceEdtions()) {
            conference.addConferenceEdition(StorageController.getInstance().retrieveConferenceEdition(i));
        }
        return conference;
    }

    public List<Conference> convertToConferenceList(List<ConferenceDTO> conferenceDTOList) throws ExecutionException, InterruptedException {
        List<Conference> conferenceList = new ArrayList<>();
        for (ConferenceDTO c : conferenceDTOList) {
            conferenceList.add(convertToConference(c));
        }
        return conferenceList;
    }
}
