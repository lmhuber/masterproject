package masterthesis.conferences.data.metrics;

import java.util.stream.Stream;

public enum ApplicationType {
    ZOOM("zoom");


    private final String text;

    ApplicationType(String text) {
        this.text = text;
    }

    public String text() {
        return this.text;
    }

    public static boolean configExists(String input) {
        return Stream.of(ApplicationType.values()).map(ApplicationType::text).anyMatch(t -> t.equals(input));
    }

    public static ApplicationType getFromString(String type) {
        switch (type) {
            case "zoom":
                return ZOOM;
            default:
                return null;
        }
    }
}
