package masterthesis.conferences.data.model;

import masterthesis.conferences.data.metrics.APIMetric;
import masterthesis.conferences.data.metrics.ApplicationType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class IngestConfiguration {
    private int id;

    private ApplicationType type;

    private Map<String, String> parameters = new HashMap<>();

    public IngestConfiguration() {

    }

    public IngestConfiguration(int id, ApplicationType type) {
        this.id = id;
        this.type = type;
        this.parameters = APIMetric.createConfigMap(type);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ApplicationType getType() {
        return type;
    }

    public void setType(ApplicationType type) {
        this.type = type;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IngestConfiguration that = (IngestConfiguration) o;
        return id == that.id && type == that.type && Objects.equals(parameters, that.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, parameters);
    }

    @Override
    public String toString() {
        return "IngestConfiguration{" +
                "id=" + id +
                ", type=" + type +
                ", parameters=" + parameters +
                '}';
    }
}
