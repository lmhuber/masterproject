package masterthesis.conferences.data.model;

import java.util.Objects;

public class AdditionalMetric {
    private int id;
    private float datapoint;
    private String metricIdentifier;

    public AdditionalMetric(int id, float datapoint, String metricIdentifier) {
        this.id = id;
        this.datapoint = datapoint;
        this.metricIdentifier = metricIdentifier;
    }

    public AdditionalMetric() {

    }

    public float getDatapoint() {
        return datapoint;
    }

    public String getMetricIdentifier() {
        return metricIdentifier;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDatapoint(float datapoint) {
        this.datapoint = datapoint;
    }

    public void setMetricIdentifier(String metricIdentifier) {
        this.metricIdentifier = metricIdentifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdditionalMetric that = (AdditionalMetric) o;
        return metricIdentifier.equals(that.metricIdentifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(datapoint, metricIdentifier);
    }

    @Override
    public String toString() {
        return "AdditionalMetric{" +
                "datapoint=" + datapoint +
                ", metricIdentifier='" + metricIdentifier + '\'' +
                '}';
    }
}
