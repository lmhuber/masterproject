package masterthesis.conferences.server.rest.service;

public interface QoSMetricsService {

    String getQOSMetrics(String meetingId);

    void getQOSMetricsForMeetings() throws InterruptedException;
}
