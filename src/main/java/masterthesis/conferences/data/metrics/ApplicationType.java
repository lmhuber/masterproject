package masterthesis.conferences.data.metrics;

import java.util.List;
import java.util.stream.Stream;

public enum ApplicationType {
    ZOOM("zoom"),
    MANUAL("manual");

    private final String text;

    private static final String MANUAL_STRING = "manual";
    private static final String ZOOM_STRING = "zoom";


    ApplicationType(String text) {
        this.text = text;
    }

    public String text() {
        return this.text;
    }

    public static boolean configExists(String input) {
        return Stream.of(ApplicationType.values()).map(ApplicationType::text).anyMatch(t -> t.equals(input));
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    public static ApplicationType getFromString(String type) {
        switch (type) {
            case ZOOM_STRING:
                return ZOOM;
            case MANUAL_STRING:
                return MANUAL;
            default:
                return null;
        }
    }

    public static List<String> getTypes(){
        return List.of(ZOOM.text, MANUAL.text);
    }
}
