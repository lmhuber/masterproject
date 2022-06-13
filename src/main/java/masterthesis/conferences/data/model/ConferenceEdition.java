package masterthesis.conferences.data.model;

import co.elastic.clients.elasticsearch._types.mapping.*;

import java.util.*;

public class ConferenceEdition {
    private final int id;
    private final int year;
    private final int edition;
    private final int participants;
    private final int sessions;
    private final int greenInnovativeness;

    private final float interactionDynamics;
    private final float cost;
    private final float carbonFootprint;

    private final String sustainability;
    private final String country;
    private final String city;

    private final HashSet<AdditionalMetric> additionalMetrics;

    private final static Map<String, Property> properties = new HashMap<>();

    public ConferenceEdition(int id, int year, int edition, int participants, int sessions,
                             int greenInnovativeness, float interactionDynamics, float cost,
                             float carbonFootprint, String sustainability, String country,
                             String city, AdditionalMetric... additionalMetrics) {
        this.id = id;
        this.year = year;
        this.edition = edition;
        this.participants = participants;
        this.sessions = sessions;
        this.greenInnovativeness = greenInnovativeness;
        this.interactionDynamics = interactionDynamics;
        this.cost = cost;
        this.carbonFootprint = carbonFootprint;
        this.sustainability = sustainability;
        this.country = country;
        this.city = city;
        this.additionalMetrics = new HashSet<>();
        this.additionalMetrics.addAll(Set.of(additionalMetrics));
    }

    public static Map<String, Property> getProperties() {
        if (properties.isEmpty()) {
            properties.put("id", new Property(IntegerNumberProperty.of(n -> n)));
            properties.put("year", new Property(IntegerNumberProperty.of(n -> n)));
            properties.put("edition", new Property(IntegerNumberProperty.of(n -> n)));
            properties.put("participants", new Property(IntegerNumberProperty.of(n -> n)));
            properties.put("sessions", new Property(IntegerNumberProperty.of(n -> n)));
            properties.put("greenInnovativeness", new Property(IntegerNumberProperty.of(n -> n)));
            properties.put("interactionDynamics", new Property(FloatNumberProperty.of(t -> t)));
            properties.put("cost", new Property(FloatNumberProperty.of(t -> t)));
            properties.put("carbonFootprint", new Property(FloatNumberProperty.of(t -> t)));
            properties.put("sustainability", new Property(TextProperty.of(t -> t)));
            properties.put("country", new Property(TextProperty.of(t -> t)));
            properties.put("additionalMetric", new Property(NestedProperty.of(t -> t)));
        }
        return properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConferenceEdition that = (ConferenceEdition) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
