package masterthesis.conferences.data;

import masterthesis.conferences.data.dto.ConferenceDTO;
import masterthesis.conferences.data.dto.ConferenceEditionDTO;
import masterthesis.conferences.data.dto.ConferenceFrontendDTO;
import masterthesis.conferences.data.model.AdditionalMetric;
import masterthesis.conferences.data.model.Conference;
import masterthesis.conferences.data.model.ConferenceEdition;
import masterthesis.conferences.server.rest.service.ConferenceService;
import masterthesis.conferences.server.rest.service.ConferenceServiceImpl;
import masterthesis.conferences.server.rest.storage.ElasticSearchOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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
                edition.getCity(), (HashMap<String, Float>) edition.getAdditionalMetrics().stream()
                .collect(Collectors.toMap(AdditionalMetric::getMetricIdentifier, AdditionalMetric::getDatapoint)));
    }

    public Conference convertToConference(ConferenceDTO conferenceDTO) throws ExecutionException, InterruptedException {
        if (conferenceDTO == null) return null;
        Conference conference = new Conference(conferenceDTO.getTitle(),
                conferenceDTO.getOrganization(),
                conferenceDTO.getPublisher());
        for (int i : conferenceDTO.getConferenceEdtions()) {
            conference.addConferenceEdition(ElasticSearchOperations.retrieveConferenceEdition(i));
        }
        return conference;
    }

    public Conference convertFrontendDTOToConference(ConferenceFrontendDTO conferenceDTO) {
        if (conferenceDTO == null) return null;
        return conferenceService.findById(conferenceDTO.getTitle());
    }

    public List<Conference> convertToConferenceList(List<ConferenceDTO> conferenceDTOList) throws ExecutionException, InterruptedException {
        List<Conference> conferenceList = new ArrayList<>();
        for (ConferenceDTO c : conferenceDTOList) {
            conferenceList.add(convertToConference(c));
        }
        return conferenceList;
    }

    public ConferenceEdition convertToConferenceEdition(ConferenceEditionDTO editionDTO) {
        if (editionDTO == null) return null;
        ConferenceEdition conferenceEdition = new ConferenceEdition();
        conferenceEdition.setEdition(editionDTO.getEdition());
        conferenceEdition.setYear(editionDTO.getYear());
        conferenceEdition.setCarbonFootprint(editionDTO.getCarbonFootprint());
        conferenceEdition.setCity(editionDTO.getCity());
        conferenceEdition.setId(editionDTO.getId());
        conferenceEdition.setCost(editionDTO.getCost());
        conferenceEdition.setCountry(editionDTO.getCountry());
        conferenceEdition.setParticipants(editionDTO.getParticipants());
        conferenceEdition.setSessions(editionDTO.getSessions());
        conferenceEdition.setCarbonFootprint(editionDTO.getCarbonFootprint());
        conferenceEdition.setGreenInnovativeness(editionDTO.getGreenInnovativeness());
        conferenceEdition.setInteractionDynamics(editionDTO.getInteractionDynamics());
        for (Map.Entry<String, Float> e : editionDTO.getAdditionalMetrics().entrySet()) {
            conferenceEdition.getAdditionalMetrics().add(new AdditionalMetric(e.getValue(), e.getKey()));
        }
        conferenceEdition.setSustainability(editionDTO.getSustainability());
        return conferenceEdition;
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
