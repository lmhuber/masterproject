package masterthesis.conferences.data.dto;

import java.util.List;

public class SelectedMetricsDTO {
    private List<String> selectedMetrics;

    public List<String> getSelectedMetrics() {
        return selectedMetrics;
    }

    public void setSelectedMetrics(List<String> selectedMetrics) {
        this.selectedMetrics = selectedMetrics;
    }
}