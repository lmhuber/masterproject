package masterthesis.conferences.server.rest.service;

import masterthesis.conferences.data.config.QoSConfig;
import org.elasticsearch.common.inject.Inject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class QoSMetricsServiceImpl implements QoSMetricsService {

    @Autowired
    private QoSConfig qoSConfig;

    @Override
    public ResponseEntity<?> getQOSMetrics(String meetingId) {
        try {
            String uri="https://apimocha.com/conferences/metrics/meetings/{meetingId}/participants/qos";
            RestTemplate restTemplate = new RestTemplate();
            Map<String, String> params = new HashMap<>();
            params.put("meetingId", meetingId);

            // create headers
            HttpHeaders headers = new HttpHeaders();

            // set content and accept headers
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            // build the request
            HttpEntity request = new HttpEntity(headers);

            ResponseEntity<Object> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    request,
                    Object.class,
                    params
            );
            return response;
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Scheduled(cron = "@daily")
    public List<ResponseEntity<?>> getQOSMetricsForMeetings() {
        List<ResponseEntity<?>> responses = null;
        try {
            List<String> meetingIdList = qoSConfig.getMeetingIdList();
            responses = new ArrayList<>();

            for (var meetingId : meetingIdList) {
                responses.add(getQOSMetrics(meetingId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responses;
    }

    // TODO: @Alex: think about how you want to save these metrics to the database
    // we can expand the application.yml and config with more parameters, if they are needed
    // for a scheduled cronjob, you can only use methods that accept no parameters, but we can read them from these files
    @Inject
    public void setQoSConfig(QoSConfig qoSConfig) {
        this.qoSConfig = qoSConfig;
    }
}
