package masterthesis.conferences.data.dto;

import co.elastic.clients.elasticsearch._types.mapping.Property;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConferenceEditionDTO {
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

    private final HashMap<String, Float> additionalMetrics;

    private final static Map<String, Property> properties = new HashMap<>();

    public ConferenceEditionDTO(int id, int year, int edition, int participants, int sessions,
                                int greenInnovativeness, float interactionDynamics, float cost,
                                float carbonFootprint, String sustainability, String country,
                                String city, HashMap<String, Float> additionalMetrics) {
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
        this.additionalMetrics = additionalMetrics;
    }

    public static Map<String, Property> getProperties() {
        if (properties.isEmpty()) {
            properties.put("id", Property.of(n -> n.long_(fn -> fn.store(true))));
            properties.put("year", Property.of(n -> n.long_(fn -> fn.store(true))));
            properties.put("edition", Property.of(n -> n.long_(fn -> fn.store(true))));
            properties.put("participants", Property.of(n -> n.long_(fn -> fn.store(true))));
            properties.put("sessions", Property.of(n -> n.long_(fn -> fn.store(true))));
            properties.put("greenInnovativeness", Property.of(n -> n.long_(fn -> fn.store(true))));
            properties.put("interactionDynamics", Property.of(n -> n.float_(fn -> fn.store(true))));
            properties.put("cost", Property.of(n -> n.float_(fn -> fn.store(true))));
            properties.put("carbonFootprint", Property.of(n -> n.float_(fn -> fn.store(true))));
            properties.put("sustainability", Property.of(n -> n.float_(fn -> fn.store(true))));
            properties.put("city", Property.of(n -> n.text(fn -> fn.store(true))));
            properties.put("country", Property.of(n -> n.text(fn -> fn.store(true))));
            properties.put("additionalMetric", Property.of(n -> n.nested(fn -> fn)));
        }
        return properties;
    }


    public int getId() {
        return id;
    }

    public int getYear() {
        return year;
    }

    public int getEdition() {
        return edition;
    }

    public int getParticipants() {
        return participants;
    }

    public int getSessions() {
        return sessions;
    }

    public int getGreenInnovativeness() {
        return greenInnovativeness;
    }

    public float getInteractionDynamics() {
        return interactionDynamics;
    }

    public float getCost() {
        return cost;
    }

    public float getCarbonFootprint() {
        return carbonFootprint;
    }

    public String getSustainability() {
        return sustainability;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public HashMap<String, Float> getAdditionalMetrics() {
        return additionalMetrics;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConferenceEditionDTO that = (ConferenceEditionDTO) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
