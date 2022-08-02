package masterthesis.conferences.data.metrics;

import java.util.List;

public interface CalculatedMetric {

    float calculateMetric(String jsonResponse);

    List<String> getConfigParameters();

    String getKey();

    String getTitle();

    ApplicationType getApplicationType();

}
