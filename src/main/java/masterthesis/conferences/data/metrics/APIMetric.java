package masterthesis.conferences.data.metrics;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public abstract class APIMetric extends Metric {
    private final List<String> jsonPath = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public APIMetric (List<String> pathValues) {
        jsonPath.addAll(pathValues);
    }

    protected List<String> fetchValuesFromResponse(String json) {
        List<String> values = new ArrayList<>();
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            values = parseJsonNode(jsonNode.get(0), 0);
        } catch (Exception e){
            e.printStackTrace();
        }
        return values;
    }

    private List<String> parseJsonNode(JsonNode json, int index) {
        List<String> values = new ArrayList<>();
        if (index == (jsonPath.size() - 1)) {
            values.add(json.get(jsonPath.get(index)).textValue());
            return values;
        }
        json = json.get(jsonPath.get(index));
        if (json.isArray()) {
            for (final JsonNode node : json) values.addAll(parseJsonNode(node, index+1));
        } else {
            values.addAll(parseJsonNode(json, index+1));
        }
        return values;
    }

    public float getValue(){
        return value;
    }

}
