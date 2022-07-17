package masterthesis.conferences.server.rest.service;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface QoSMetricsService {

    ResponseEntity<?> getQOSMetrics(String meetingId);

    List<ResponseEntity<?>> getQOSMetricsForMeetings();
}
