package masterthesis.conferences.data.model.dto;

import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO class, which is needed to transfer all selected metrics for a dashboard
 */
public class SelectedMetricsDTO {
    private List<DashboardingMetricDTO> selectedMetrics;

    public List<DashboardingMetricDTO> getSelectedMetrics() {
        return selectedMetrics;
    }

    public List<DashboardingMetricDTO> getOnlySelectedMetrics() {
        return selectedMetrics.stream().filter(DashboardingMetricDTO::getChecked).collect(Collectors.toList());
    }

    public void setSelectedMetrics(List<DashboardingMetricDTO> selectedMetrics) {
        this.selectedMetrics = selectedMetrics;
    }

    public SelectedMetricsDTO() {
    }

    public SelectedMetricsDTO(List<DashboardingMetricDTO> selectedMetrics) {
        this.selectedMetrics = selectedMetrics;
    }
}