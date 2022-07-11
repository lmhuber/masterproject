package masterthesis.conferences.data.util;

public enum Indices {

    CONFERENCE("conference"),
    CONFERENCE_EDITION("conference-edition"),
    ADDITIONAL_METRIC("conference-additional-metric");

    private final String indexName;

    Indices(String indexName) {
        this.indexName = indexName;
    }

    public String indexName() {
        return this.indexName;
    }
}
