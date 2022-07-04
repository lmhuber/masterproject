package masterthesis.conferences.server.dashboarding;

import masterthesis.conferences.data.dto.DashboardingMetricDTO;
import masterthesis.conferences.data.model.Conference;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static masterthesis.conferences.server.controller.ConferenceController.JSON_EXPORT;
import static masterthesis.conferences.server.dashboarding.ChartType.METRIC;
import static masterthesis.conferences.server.dashboarding.DashboardingConstants.*;

public class DashboardingUtils {

    public static void convertToDashboard(Conference conference, List<DashboardingMetricDTO> dashboardingMetrics) {
        String preparedJson;
        String importJson;
        try {
            String json = new String(Files.readAllBytes(Paths.get(JSON_EXPORT + "template.txt")));
            preparedJson = prepareDashboardImport(conference, json);

            if (dashboardingMetrics != null && !dashboardingMetrics.isEmpty()) {
                StringBuilder toAppendMetrics = new StringBuilder();
                StringBuilder toAppendRefs = new StringBuilder();
                int elementCounter = 0;
                for (DashboardingMetricDTO metric : dashboardingMetrics) {
                    toAppendMetrics.append(",");
                    toAppendRefs.append(",");
                    convertPanel(conference, metric, toAppendMetrics, toAppendRefs, elementCounter);
                    elementCounter++;
                }
                importJson = replaceAdditionalMetricsMarkers(preparedJson,
                        toAppendMetrics.toString(), toAppendRefs.toString());
            } else {
                importJson = replaceAdditionalMetricsMarkers(preparedJson, "", "");
            }

            try (FileWriter fWriter = new FileWriter(JSON_EXPORT + "export.ndjson")) {
                fWriter.write(importJson);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void convertPanel(Conference conference, DashboardingMetricDTO metric,
                                     StringBuilder toAppendMetrics, StringBuilder toAppendRefs,
                                     int elementCounter) throws IOException {
        String panel;
        if (metric.getDashboardType().equals(METRIC.getName())) {
            panel = new String(Files.readAllBytes(Paths.get(JSON_EXPORT + "metric.json")));
        } else {
            String tempPanel = new String(Files.readAllBytes(Paths.get(JSON_EXPORT + "line_bar.json")));
            panel = tempPanel.replaceAll(METRIC_CHART_TYPE, metric.getDashboardType());
        }
        final String index = conference.getTitle()+"-index-"+metric.getTitle();
        final String layer = conference.getTitle()+"-layer-"+metric.getTitle();
        String panelIndex = panel.replaceAll(METRIC_INDEX, index);
        String panelTitle = panelIndex.replaceAll(METRIC_TITLE, metric.getPanelTitle());
        String panelBaseX = panelTitle.replaceAll(METRIC_BASE_X, calcBaseX(elementCounter));
        String panelBaseY = panelBaseX.replaceAll(METRIC_BASE_Y, calcBaseY(elementCounter));
        String panelW = panelBaseY.replaceAll(METRIC_W, String.valueOf(WIDTH));
        String panelH = panelW.replaceAll(METRIC_H, String.valueOf(HEIGHT));
        String panelLayer = panelH.replaceAll(METRIC_LAYER, layer);
        String panelColumn = panelLayer.replaceAll(METRIC_COLUMN,
                conference.getTitle()+"-column-"+metric.getTitle());
        String panelLabel = panelColumn.replaceAll(METRIC_DB_LABEL,
                toUpper(metric.getOperation())+" of "+toUpper(metric.getTitle()));
        String panelDataType = panelLabel.replaceAll(METRIC_DB_DATATYPE, "number");
        String panelOperation = panelDataType.replaceAll(METRIC_DB_OPERATION, metric.getOperation());
        toAppendMetrics.append(panelOperation.replaceAll(METRIC_DB_FIELD,
                METRIC_DB_FIELD_PREFIX + metric.getTitle()+"}"));

        String refs = new String(Files.readAllBytes(Paths.get(JSON_EXPORT + "refs.txt"))).replaceAll(METRIC_INDEX,
                index);
        toAppendRefs.append(refs.replaceAll(METRIC_LAYER, layer));

    }

    private static String toUpper(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private static String calcBaseX(int elementCounter) {
        return String.valueOf(elementCounter % 2 == 0 ? BASE_X_COL_1 : BASE_X_COL_2);
    }

    private static String calcBaseY(int elementCounter) {
        int y = BASE_Y_COL_1;
        if (elementCounter % 2 != 0) y = BASE_Y_COL_2;
        return String.valueOf(y + ((elementCounter % 2) * HEIGHT));
    }

    private static String replaceAdditionalMetricsMarkers(String json, String metrics, String refs) {
        String metricsEscaped = metrics.replaceAll("\"", "\\\"");
        String jsonReplacedMetrics = json.replaceAll(PANEL_MARKER, metricsEscaped);
        return jsonReplacedMetrics.replaceAll(REFS_MARKER, refs);
    }

    private static String prepareDashboardImport(Conference conference, String json) {
        String jsonReplaced = json.replaceAll(DB_ID, conferenceAsIdList(conference.getConferenceEditionIds()));
        String jsonReplaced2 = jsonReplaced.replaceAll(DB_DASHBOARD, conference.getTitle() + "-dashboard");
        return jsonReplaced2.replaceAll(DB_TITLE, DB_TITLE_PREFIX + conference.getTitle());
    }

    private static String conferenceAsIdList(Set<Integer> conferenceEditionIds) {
        StringBuilder string = new StringBuilder();
        List<Integer> ids = new ArrayList<>(conferenceEditionIds);
        for (int i = 0; i < conferenceEditionIds.size(); i++) {
            if (i != 0) {
                string.append(" or ");
            }
            string.append(ids.get(i));
        }
        return string.toString();
    }

}
