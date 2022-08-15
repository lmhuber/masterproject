package masterthesis.conferences.data.metrics;

import masterthesis.conferences.data.metrics.zoom.AudioLatency;

import java.util.HashMap;
import java.util.Map;

public abstract class Metric implements CalculatedMetric {
    protected float value;

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    public static Map<String, String> createConfigMap(ApplicationType type) {
        Map<String, String> config = new HashMap<>();
        Metric metric = null;
        switch (type) {
            case ZOOM:
                metric = new AudioLatency();
                break;
            case MANUAL:
                metric = new ManualMetric("manual");
        }
        assert metric != null;
        for (String text : metric.getConfigParameters()) {
            config.put(text, "");
        }
        return config;
    }
}
