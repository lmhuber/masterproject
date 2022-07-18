package masterthesis.conferences.data.metrics;

import java.util.List;

public interface CalculatedMetric {

    public float calculateMetric(String jsonResponse);

    public List<String> getConfigParameters();

    public String getTitle();
}
