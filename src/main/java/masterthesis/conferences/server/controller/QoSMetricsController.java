package masterthesis.conferences.server.controller;

import masterthesis.conferences.server.rest.service.QoSMetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping(value = "/qosmetrics")
public class QoSMetricsController {

    @Autowired
    QoSMetricsService qoSMetricsService;

    @GetMapping("/single")
    public String getQOSMetrics(String meetingId) {
        return qoSMetricsService.getQOSMetrics(meetingId);
    }

    public void getQOSMetricsForMeetings() throws InterruptedException, ExecutionException {
        qoSMetricsService.getQOSMetricsForMeetings();
    }
}