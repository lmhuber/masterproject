package masterthesis.conferences.data;

import masterthesis.conferences.data.dto.*;
import masterthesis.conferences.data.model.AdditionalMetric;
import masterthesis.conferences.data.model.Conference;
import masterthesis.conferences.data.model.ConferenceEdition;
import masterthesis.conferences.data.model.IngestConfiguration;
import masterthesis.conferences.server.rest.service.ConferenceService;
import masterthesis.conferences.server.rest.service.ConferenceServiceImpl;
import masterthesis.conferences.server.rest.storage.ElasticReadOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class MapperService {
    @Autowired
    private final ConferenceService conferenceService = new ConferenceServiceImpl();

    public MapperService() {
    }

    public ConferenceDTO convertToConferenceDTO(String title) {
        Conference conference = conferenceService.findById(title);
        return new ConferenceDTO(conference.getTitle(), conference.getOrganization(),
                conference.getPublisher(), conference.getConferenceEditionIds());
    }

    public ConferenceEditionDTO convertToConferenceEditionDTO(int id) {
        ConferenceEdition edition = conferenceService.findById(id);
        return new ConferenceEditionDTO(id, edition.getYear(), edition.getEdition(), edition.getParticipants(),
                edition.getSessions(), edition.getGreenInnovativeness(), edition.getInteractionDynamics(),
                edition.getCost(), edition.getCarbonFootprint(), edition.getSustainability(), edition.getCountry(),
                edition.getCity(), (HashSet<Integer>) edition.getAdditionalMetricIds());
    }

    public AdditionalMetricDTO convertToAdditionalMetricDTO(int id) {
        AdditionalMetric metric = conferenceService.findByMetricId(id);
        return new AdditionalMetricDTO(metric.getId(), metric.getConfig().getId(),
                conferenceService.findEditionByMetricId(id).getId(),
                metric.getDatapoint(), metric.getMetricIdentifier());
    }

    public IngestConfigurationDTO convertToIngestConfigurationDTO(int id) {
        IngestConfiguration config = conferenceService.findConfigById(id);
        if (config == null) return null;
        return new IngestConfigurationDTO(config.getId(), config.getType().text(), config.getParameters());
    }

    public Conference convertToConference(ConferenceDTO conferenceDTO) throws ExecutionException, InterruptedException {
        if (conferenceDTO == null) return null;
        Conference conference = new Conference(conferenceDTO.getTitle(),
                conferenceDTO.getOrganization(),
                conferenceDTO.getPublisher());
        for (int i : conferenceDTO.getConferenceEdtions()) {
            conference.addConferenceEdition(ElasticReadOperation.retrieveConferenceEdition(i));
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

    public ConferenceEdition convertFrontendDTOToConferenceEdition(ConferenceFrontendDTO editionDTO) {
        if (editionDTO == null) return null;
        ConferenceEdition conferenceEdition = conferenceService.findById(editionDTO.getId());
        conferenceEdition.setEdition(editionDTO.getEdition());
        conferenceEdition.setYear(editionDTO.getYear());
        conferenceEdition.setCity(editionDTO.getCity());
        conferenceEdition.setId(editionDTO.getId());
        conferenceEdition.setCountry(editionDTO.getCountry());
        conferenceEdition.setParticipants(editionDTO.getParticipants());
        conferenceEdition.setSessions(editionDTO.getSessions());
        return conferenceEdition;
    }

    public ConferenceFrontendDTO convertToFrontendDTO(String title, int id) {
        ConferenceFrontendDTO frontendDTO = new ConferenceFrontendDTO();
        Conference conference = conferenceService.findById(title);
        ConferenceEdition edition = conferenceService.findById(id);
        frontendDTO.setCity(edition.getCity());
        frontendDTO.setCountry(edition.getCountry());
        frontendDTO.setEdition(edition.getEdition());
        frontendDTO.setOrganization(conference.getOrganization());
        frontendDTO.setPublisher(conference.getPublisher());
        frontendDTO.setParticipants(edition.getParticipants());
        frontendDTO.setId(edition.getId());
        frontendDTO.setSessions(edition.getSessions());
        frontendDTO.setYear(edition.getYear());
        frontendDTO.setTitle(conference.getTitle());
        return frontendDTO;
    }
}
