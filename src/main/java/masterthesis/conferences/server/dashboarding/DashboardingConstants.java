package masterthesis.conferences.server.dashboarding;

public class DashboardingConstants{
    public static final int BASE_X_COL_1 = 0;
    public static final int BASE_X_COL_2 = 24;
    public static final int WIDTH = 24;
    public static final int HEIGHT = 15;
    public static final int BASE_Y_COL_1 = 35;
    public static final int BASE_Y_COL_2 = 26;

    public static final String PANEL_MARKER = "<ADDITIONAL-PANELS>";
    public static final String REFS_MARKER = "<ADDITIONAL-REFS>";
    public static final String DB_ID = "<CHANGEME-ID>";
    public static final String DB_DASHBOARD = "<CHANGEME-Dashboard>";
    public static final String DB_TITLE = "<CHANGEME-Title>";
    public static final String DB_TITLE_PREFIX = "Conference Overview - ";
    public static final String METRIC_INDEX = "<INDEX>";
    public static final String METRIC_TITLE = "<TITLE>";
    public static final String METRIC_BASE_X = "\"<BASE_X>\"";
    public static final String METRIC_BASE_Y = "\"<BASE_Y>\"";
    public static final String METRIC_W = "\"<W>\"";
    public static final String METRIC_H = "\"<H>\"";
    public static final String METRIC_LAYER = "<LAYER>";
    public static final String METRIC_COLUMN = "<COLUMN>";
    // aligns to masterthesis.conferences.server.dashboarding.ChartType
    public static final String METRIC_CHART_TYPE = "<TYPE>";
    public static final String METRIC_DB_LABEL = "<DB-LABEL>";
    public static final String METRIC_DB_DATATYPE = "<DB-DATATYPE>";
    // aligns to masterthesis.conferences.server.dashboarding.Operations
    public static final String METRIC_DB_OPERATION = "<DB-OPERATION>";
    // see also https://www.elastic.co/guide/en/kibana/8.3/kuery-query.html
    public static final String METRIC_DB_FIELD = "<DB-FIELD>";
    public static final String METRIC_DB_QUERY = "<METRIC-QUERY>";
}
