package masterthesis.conferences.metrics;

import masterthesis.conferences.data.metrics.zoom.AudioLatency;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class APIMetricTests {

    public static final String TEST_JSON_RESPONSE = "src/main/resources/testing/response.json";

    @Test
    void parseAudioLatency() throws IOException {
        AudioLatency latency = new AudioLatency();
        assertEquals(latency.calculateMetric(
                new String(Files.readAllBytes(Paths.get(TEST_JSON_RESPONSE)))),
                2.0f);
        assertEquals(latency.getValue(), 2.0f);
    }
}
