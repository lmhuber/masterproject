package masterthesis.conferences.data.model;

import masterthesis.conferences.data.metrics.APIMetric;

import java.util.Objects;

public class AdditionalMetric {
    private int id;
    private IngestConfiguration config;
    private float datapoint;
    private String metricIdentifier;

    public AdditionalMetric(int id, IngestConfiguration ingestConfiguration, float datapoint, String metricIdentifier) {
        this.id = id;
        this.config = ingestConfiguration;
        this.datapoint = datapoint;
        this.metricIdentifier = metricIdentifier;
    }

    public AdditionalMetric(int id, int ingestId, APIMetric metric) {
        this.id = id;
        this.config = new IngestConfiguration(ingestId, metric.getApplicationType());
        this.metricIdentifier = metric.getTitle();
        this.datapoint = 0.0f;
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

    public IngestConfiguration getConfig() {
        return config;
    }

    public void setConfig(IngestConfiguration config) {
        this.config = config;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdditionalMetric that = (AdditionalMetric) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AdditionalMetric{" +
                "id=" + id +
                ", config=" + config +
                ", datapoint=" + datapoint +
                ", metricIdentifier='" + metricIdentifier + '\'' +
                '}';
    }
}
