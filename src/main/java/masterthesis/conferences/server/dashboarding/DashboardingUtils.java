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
            panel = new String(Files.readAllBytes(Paths.get(JSON_EXPORT + "line_bar.json")))
                    .replaceAll(METRIC_CHART_TYPE, metric.getDashboardType());
        }
        final String index = conference.getTitle()+"-index-"+metric.getTitle();
        final String layer = conference.getTitle()+"-layer-"+metric.getTitle();
        toAppendMetrics.append(panel.replaceAll(METRIC_INDEX, index)
                .replaceAll(METRIC_TITLE, metric.getPanelTitle())
                .replaceAll(METRIC_BASE_X, calcBaseX(elementCounter))
                .replaceAll(METRIC_BASE_Y, calcBaseY(elementCounter))
                .replaceAll(METRIC_W, String.valueOf(WIDTH))
                .replaceAll(METRIC_H, String.valueOf(HEIGHT))
                .replaceAll(METRIC_LAYER, layer)
                .replaceAll(METRIC_COLUMN, conference.getTitle()+"-column-"+metric.getTitle())
                .replaceAll(METRIC_DB_LABEL, toUpper(metric.getOperation())+" of "+toUpper(metric.getTitle()))
                .replaceAll(METRIC_DB_DATATYPE, "number")
                .replaceAll(METRIC_DB_OPERATION, metric.getOperation())
                .replaceAll(METRIC_DB_FIELD, METRIC_DB_FIELD_PREFIX + metric.getTitle()+"}"));
        toAppendRefs.append(new String(Files.readAllBytes(Paths.get(JSON_EXPORT + "refs.txt")))
                .replaceAll(METRIC_INDEX, index)
                .replaceAll(METRIC_LAYER, layer));

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
        return json.replaceAll(PANEL_MARKER, metrics.replaceAll("\"", "\\\""))
                .replaceAll(REFS_MARKER, refs);
    }

    private static String prepareDashboardImport(Conference conference, String json) {
        return json.replaceAll(DB_ID, conferenceAsIdList(conference.getConferenceEditionIds()))
                .replaceAll(DB_DASHBOARD, conference.getTitle() + "-dashboard")
                .replaceAll(DB_TITLE, DB_TITLE_PREFIX + conference.getTitle());
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
