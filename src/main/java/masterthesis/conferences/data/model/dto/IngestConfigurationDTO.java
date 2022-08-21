package masterthesis.conferences.data.model.dto;

import co.elastic.clients.elasticsearch._types.mapping.Property;
import masterthesis.conferences.data.metrics.ApplicationType;
import masterthesis.conferences.data.model.IngestConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
/**
 * DTO class, which is needed to transfer ingest configurations
 */
public class IngestConfigurationDTO {
    private int id;
    private String type;
    private Map<String, String> parameters = new HashMap<>();

    private final static Map<String, Property> properties = new HashMap<>();

    public IngestConfigurationDTO(IngestConfiguration config) {
        this.id = config.getId();
        this.type = config.getType().text();
        this.parameters = config.getParameters();
    }

    public IngestConfigurationDTO(int id, String type, Map<String, String> parameters) {
        this.id = id;
        this.type = type;
        this.parameters = parameters;
    }

    public IngestConfigurationDTO() {

    }

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

    public static IngestConfiguration convertToIngestConfiguration(IngestConfigurationDTO configDTO) {
        if (configDTO == null) return null;
        IngestConfiguration config = new IngestConfiguration(configDTO.getId(), ApplicationType.getFromString(configDTO.getType()));
        if (!configDTO.getParameters().isEmpty()) config.setParameters(configDTO.getParameters());
        return config;
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

    public static Map<String, Property> getProperties() {
        if (properties.isEmpty()) {
            properties.put("id", Property.of(c -> c.long_(fn -> fn.store(true))));
            properties.put("type", Property.of(c -> c.text(fn -> fn.store(true).fields("raw", Property.of(k -> k.keyword(fn2 -> fn2))))));
            properties.put("parameters", Property
                    .of(n -> n.nested(fn -> fn.properties(Map
                            .of("config", Property.of(tn -> tn.text(tfn -> tfn.store(true))),
                                    "value",
                                    Property.of(tn -> tn.text(tfn -> tfn.store(true))))))));

        }
        return properties;
    }
}
