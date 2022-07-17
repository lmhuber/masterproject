package masterthesis.conferences.server.controller;

import masterthesis.conferences.server.rest.service.QoSMetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/qosmetrics")
public class QoSMetricsController {

    @Autowired
    QoSMetricsService qoSMetricsService;

    @GetMapping("/single")
    public ResponseEntity<?> getQOSMetrics(String meetingId) {
        return qoSMetricsService.getQOSMetrics(meetingId);
    }

    @GetMapping("/multiple")
    public List<ResponseEntity<?>> getQOSMetricsForMeetings() {
        return qoSMetricsService.getQOSMetricsForMeetings();
    }
}