package masterthesis.conferences.data.dto;

import co.elastic.clients.elasticsearch._types.mapping.Property;
import masterthesis.conferences.data.model.AdditionalMetric;
import masterthesis.conferences.server.rest.storage.ElasticReadOperation;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class AdditionalMetricDTO {
    private int metId;
    private int conferenceEdition;
    private int ingestConfigId;
    private float datapoint;
    private String metricIdentifier;

    private final static Map<String, Property> properties = new HashMap<>();

    public AdditionalMetricDTO() {
    }

    public AdditionalMetricDTO(int metId, int conferenceEdition, int ingestConfigId, float datapoint, String metricIdentifier) {
        this.metId = metId;
        this.ingestConfigId = ingestConfigId;
        this.conferenceEdition = conferenceEdition;
        this.datapoint = datapoint;
        this.metricIdentifier = metricIdentifier;
    }

    public float getDatapoint() {
        return datapoint;
    }

    public void setDatapoint(float datapoint) {
        this.datapoint = datapoint;
    }

    public String getMetricIdentifier() {
        return metricIdentifier;
    }

    public void setMetricIdentifier(String metricIdentifier) {
        this.metricIdentifier = metricIdentifier;
    }

    public int getMetId() {
        return metId;
    }

    public void setMetId(int metId) {
        this.metId = metId;
    }

    public int getConferenceEdition() {
        return conferenceEdition;
    }

    public void setConferenceEdition(int conferenceEdition) {
        this.conferenceEdition = conferenceEdition;
    }

    public int getIngestConfigId() {
        return ingestConfigId;
    }

    public void setIngestConfigId(int ingestConfigId) {
        this.ingestConfigId = ingestConfigId;
    }

    public static AdditionalMetric convertToAdditionalMetric(AdditionalMetricDTO metricDTO) throws ExecutionException, InterruptedException {
        if (metricDTO == null) return null;
        AdditionalMetric metric = new AdditionalMetric();
        metric.setId(metricDTO.getMetId());
        metric.setMetricIdentifier(metricDTO.getMetricIdentifier());
        metric.setDatapoint(metricDTO.getDatapoint());
        metric.setConfig(ElasticReadOperation.retrieveIngestConfiguration(metricDTO.getIngestConfigId()));
        return metric;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdditionalMetricDTO that = (AdditionalMetricDTO) o;
        return metId == that.metId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(metId, ingestConfigId, conferenceEdition, datapoint, metricIdentifier);
    }

    @Override
    public String toString() {
        return "AdditionalMetricDTO{" +
                "metId=" + metId +
                ", conferenceEdition=" + conferenceEdition +
                ", ingestConfigId=" + ingestConfigId +
                ", datapoint=" + datapoint +
                ", metricIdentifier='" + metricIdentifier + '\'' +
                '}';
    }

    public static Map<String, Property> getProperties() {
        if (properties.isEmpty()) {
            properties.put("metId", Property.of(c -> c.long_(fn -> fn.store(true))));
            properties.put("ingestConfigId", Property.of(c -> c.long_(fn -> fn.store(true))));
            properties.put("metricIdentifier", Property.of(c -> c.text(fn -> fn.store(true).fields("raw", Property.of(k -> k.keyword(fn2 -> fn2))))));
            properties.put("datapoint", Property.of(n -> n.float_(fn -> fn.store(true).nullValue(-1.0f))));
            properties.put("conferenceEdition", Property.of(c -> c.long_(fn -> fn.store(true))));
        }
        return properties;
    }

}
