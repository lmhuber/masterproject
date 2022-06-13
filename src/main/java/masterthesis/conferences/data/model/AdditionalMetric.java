package masterthesis.conferences.data.model;

import java.util.Objects;

public class AdditionalMetric {
    private final float datapoint;
    private final String metricIdentifier;

    public AdditionalMetric(float datapoint, String metricIdentifier) {
        this.datapoint = datapoint;
        this.metricIdentifier = metricIdentifier;
    }

    public float getDatapoint() {
        return datapoint;
    }

    public String getMetricIdentifier() {
        return metricIdentifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdditionalMetric that = (AdditionalMetric) o;
        return Float.compare(that.datapoint, datapoint) == 0 && metricIdentifier.equals(that.metricIdentifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(datapoint, metricIdentifier);
    }
}
