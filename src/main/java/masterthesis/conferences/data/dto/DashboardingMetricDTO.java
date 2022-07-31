package masterthesis.conferences.data.dto;

import java.util.Objects;

public class DashboardingMetricDTO {
    private String title;
    private String dashboardType;
    private String operation;
    private String panelTitle;
    private boolean checked;

    public DashboardingMetricDTO() {

    }

    public DashboardingMetricDTO(String title, String dashboardType, String operation, String panelTitle, boolean checked) {
        this.title = title;
        this.dashboardType = dashboardType;
        this.operation = operation;
        this.panelTitle = panelTitle;
        this.checked = checked;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDashboardType() {
        return dashboardType;
    }

    public void setDashboardType(String dashboardType) {
        this.dashboardType = dashboardType;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getPanelTitle() {
        return panelTitle;
    }

    public void setPanelTitle(String panelTitle) {
        this.panelTitle = panelTitle;
    }

    public boolean getChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DashboardingMetricDTO that = (DashboardingMetricDTO) o;
        return Objects.equals(title, that.title) && Objects.equals(dashboardType, that.dashboardType) && Objects.equals(operation, that.operation) && Objects.equals(panelTitle, that.panelTitle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, dashboardType, operation, panelTitle);
    }
}
