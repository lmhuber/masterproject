package masterthesis.conferences.data.metrics;

import masterthesis.conferences.data.metrics.zoom.AudioLatency;

import java.util.HashMap;
import java.util.Map;

public abstract class Metric implements CalculatedMetric {
    protected float value;

    /**
     * For the actual metric, a map is needed to fetch all configuration parameters.
     * Key is the parameter name
     * Value is the parameter value
     *
     * @param type the application type from which the config is needed
     * @return a map containing the configuration for the calculation of said metric
     */
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
