package masterthesis.conferences.data.metrics.zoom;

import masterthesis.conferences.data.metrics.APIMetric;

import java.util.List;

public class AudioLatency extends APIMetric {

    public AudioLatency() {
        super(List.of("body", "participants", "user_qos", "audio_output", "latency"));
    }

    @Override
    public float calculateMetric(String jsonResponse) {
        List<String> values = fetchValuesFromResponse(jsonResponse);
        float sum = values.stream().map(s -> Float.parseFloat(s.replace(" ms", "")))
                .reduce(0.0f, (f1, f2) -> f1 + f2);
        this.value = sum / values.size();
        return super.getValue();
    }

    @Override
    public List<String> getConfigParameters() {
        return List.of("meetingId");
    }

    @Override
    public String getTitle() {
        return "audioLatency";
    }
}
