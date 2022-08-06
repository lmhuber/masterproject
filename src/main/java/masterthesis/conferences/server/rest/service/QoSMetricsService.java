package masterthesis.conferences.server.rest.service;

import java.util.concurrent.ExecutionException;

public interface QoSMetricsService {

    String getQOSMetrics(String meetingId);

    void getQOSMetricsForMeetings() throws InterruptedException, ExecutionException;
}
