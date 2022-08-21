package masterthesis.conferences.server.rest.service;

import java.util.concurrent.ExecutionException;

public interface QoSMetricsService {

    /**
     * Fetches all QOS metrics from a specific meeting id
     *
     * @param meetingId the meeting id used for the request
     * @return response of the API call
     */
    String getQOSMetrics(String meetingId);

    /**
     * Method used to fetch all QOS metrics for zoom meetings
     *
     * @throws InterruptedException
     * @throws ExecutionException
     */
    void getQOSMetricsForMeetings() throws InterruptedException, ExecutionException;
}
