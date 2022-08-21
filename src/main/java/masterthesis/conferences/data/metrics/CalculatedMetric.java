package masterthesis.conferences.data.metrics;

import java.util.List;

public interface CalculatedMetric {

    /**
     * Calculates a metric when it is fetched from an API
     *
     * @param jsonResponse API response in JSON format
     * @return metric value
     */
    float calculateMetric(String jsonResponse);

    /**
     * Returns a list of all possible configuration parameters to use
     *
     * @return titles or keys of config parameters
     */
    List<String> getConfigParameters();

    /**
     * Returns the metric key
     *
     * @return key
     */
    String getKey();

    /**
     * Returns the metric title
     *
     * @return title
     */
    String getTitle();

    /**
     * Returns the application type
     *
     * @return application type
     */
    ApplicationType getApplicationType();

}
