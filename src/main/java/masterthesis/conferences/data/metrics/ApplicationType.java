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

    /**
     * Reads all possible application types and checks if there is an configuration available
     *
     * @param input key of the input
     * @return true for an existing config, false otherwise
     */
    public static boolean configExists(String input) {
        return Stream.of(ApplicationType.values()).map(ApplicationType::text).anyMatch(t -> t.equals(input));
    }

    /**
     * Returns the application type from its title
     *
     * @param type the application types' title
     * @return the corresponding application type
     */
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

    /**
     * Retrieves a list of all available application types
     *
     * @return titles of all application types
     */
    public static List<String> getTypes(){
        return List.of(ZOOM.text, MANUAL.text);
    }
}
