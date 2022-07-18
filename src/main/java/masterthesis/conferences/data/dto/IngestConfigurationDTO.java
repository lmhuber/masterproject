package masterthesis.conferences.data.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class IngestConfigurationDTO {
    private int id;
    private String type;
    private Map<String, String> parameters = new HashMap<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
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
        IngestConfigurationDTO that = (IngestConfigurationDTO) o;
        return id == that.id && Objects.equals(type, that.type) && Objects.equals(parameters, that.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, parameters);
    }
}
