package masterthesis.conferences.server.dashboarding;

import java.util.List;

public enum ChartType {

    BAR("bar"),
    LINE("line"),
    METRIC("Metric");

    private final String name;

    ChartType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static List<String> getChartTypes(){
        return List.of(BAR.name, LINE.name, METRIC.name);
    }

}
