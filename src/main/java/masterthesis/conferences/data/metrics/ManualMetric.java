package masterthesis.conferences.data.metrics;

import java.util.ArrayList;
import java.util.List;

public class ManualMetric extends Metric {
    private String key;

    public ManualMetric(String key) {
        this.key = key;
    }
    @Override
    public float calculateMetric(String jsonResponse) {
        return 0;
    }

    @Override
    public List<String> getConfigParameters() {
        return new ArrayList<>();
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getTitle() {
        return key;
    }

    @Override
    public ApplicationType getApplicationType() {
        return ApplicationType.MANUAL;
    }
}
