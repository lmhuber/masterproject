package masterthesis.conferences.data.util;

public enum Indices {

    CONFERENCE("conference"),
    CONFERENCE_EDITION("conference-edition"),
    ADDITIONAL_METRIC("conference-additional-metric"),
    INGEST_CONFIGURATION("index-configuration");

    private final String index;

    Indices(String index) {
        this.index = index;
    }

    public String index() {
        return this.index;
    }
}
