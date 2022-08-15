package masterthesis.conferences.server.rest.service;

import masterthesis.conferences.data.metrics.APIMetric;
import masterthesis.conferences.data.metrics.ApplicationType;
import masterthesis.conferences.data.metrics.zoom.AudioLatency;
import masterthesis.conferences.data.model.AdditionalMetric;
import masterthesis.conferences.data.model.Conference;
import masterthesis.conferences.data.model.ConferenceEdition;
import masterthesis.conferences.data.model.dto.AdditionalMetricDTO;
import masterthesis.conferences.server.controller.storage.StorageController;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static masterthesis.conferences.data.metrics.zoom.AudioLatency.MEETING_ID;

@Service
public class QoSMetricsServiceImpl implements QoSMetricsService {

    private static final StorageController storageController = StorageController.getInstance();

    @Override
    public String getQOSMetrics(String meetingId) {
        try {
            String uri="http://localhost:8000/v2/metrics/meetings/:meetingId/participants/qos";
            RestTemplate restTemplate = new RestTemplate();
            Map<String, String> params = new HashMap<>();
            params.put(MEETING_ID, meetingId);

            // create headers
            HttpHeaders headers = new HttpHeaders();

            // set content and accept headers
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            // build the request
            HttpEntity<?> request = new HttpEntity<>(headers);

            ResponseEntity<Object> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    request,
                    Object.class,
                    params
            );
            return response.toString();
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR).toString();
        }
    }

    @Override
    @Scheduled(cron = "@daily")
    public void getQOSMetricsForMeetings() throws InterruptedException, ExecutionException {
        HashMap<Integer, String> metrics = new HashMap<>();
        for (Conference conference : StorageController.getInstance().getConferences()) {
            for (ConferenceEdition edition : conference.getConferenceEditions()) {
                for (AdditionalMetric metric : edition.getAdditionalMetrics()) {
                    if (metric.getConfig().getType() == ApplicationType.ZOOM &&
                            !metric.getConfig().getParameters().get(MEETING_ID).equals("")) {
                        metrics.put(metric.getId(), metric.getConfig().getParameters().get(MEETING_ID));
                    }
                }
            }
        }
        SortedSet<String> meetingIds = new TreeSet<>(metrics.values());
        for (String meetingId : meetingIds) {
            // additional metrics to differ here, when needed
            APIMetric apiMetric = new AudioLatency();
            apiMetric.calculateMetric(getQOSMetrics(meetingId));
            SortedSet<Integer> metricIds = metrics.entrySet()
                    .stream()
                    .filter(e -> e.getValue().equals(meetingId)).map(Map.Entry::getKey).collect(Collectors.toCollection(TreeSet::new));
            for (int id : metricIds) {
                AdditionalMetricDTO dto = Objects.requireNonNull(StorageController.getMapper()).convertToAdditionalMetricDTO(id);
                dto.setDatapoint(apiMetric.getValue());
                ConferenceEdition edition = storageController.getEdition(dto.getConferenceEdition());
                Conference conference =
                        storageController.getConferences()
                                .stream()
                                .filter(c -> c.getConferenceEditionIds().contains(edition.getId())).findFirst().orElse(null);
                if (conference != null) {
                    storageController.indexAdditionalMetric(edition,
                            conference, AdditionalMetricDTO.convertToAdditionalMetric(dto));
                }
            }
        }
    }
}
