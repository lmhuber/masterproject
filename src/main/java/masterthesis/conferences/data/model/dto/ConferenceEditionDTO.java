package masterthesis.conferences.data.model.dto;

import co.elastic.clients.elasticsearch._types.mapping.Property;
import masterthesis.conferences.data.model.ConferenceEdition;
import masterthesis.conferences.server.controller.storage.StorageController;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * DTO class, which is needed to transfer conference editions
 */
public class ConferenceEditionDTO {
    private int id;
    private int year;
    private int edition;
    private int participants;
    private int sessions;
    private int greenInnovativeness;

    private float interactionDynamics;
    private float cost;
    private float carbonFootprint;

    private String sustainability;
    private String country;
    private String city;

    private final HashSet<Integer> additionalMetrics = new HashSet<>();

    private final static Map<String, Property> properties = new HashMap<>();

    public ConferenceEditionDTO() {

    }

    public ConferenceEditionDTO(int id, ConferenceEdition edition) {
        this.id = id;
        this.year = edition.getYear();
        this.edition = edition.getEdition();
        this.participants = edition.getParticipants();
        this.sessions = edition.getSessions();
        this.greenInnovativeness = edition.getGreenInnovativeness();
        this.interactionDynamics = edition.getInteractionDynamics();
        this.cost = edition.getCost();
        this.carbonFootprint = edition.getCarbonFootprint();
        this.sustainability = edition.getSustainability();
        this.country = edition.getCountry();
        this.city = edition.getCity();
        this.additionalMetrics.addAll(edition.getAdditionalMetricIds());
    }

    public ConferenceEditionDTO(int id, int year, int edition, int participants, int sessions,
                                int greenInnovativeness, float interactionDynamics, float cost,
                                float carbonFootprint, String sustainability, String country,
                                String city, HashSet<Integer> additionalMetrics) {
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
        this.additionalMetrics.addAll(additionalMetrics);
    }

    public static Map<String, Property> getProperties() {
        if (properties.isEmpty()) {
            properties.put("id", Property.of(n -> n.long_(fn -> fn.store(true))));
            properties.put("year", Property.of(n -> n.long_(fn -> fn.store(true).nullValue(-1L))));
            properties.put("edition", Property.of(n -> n.long_(fn -> fn.store(true).nullValue(-1L))));
            properties.put("participants", Property.of(n -> n.long_(fn -> fn.store(true).nullValue(-1L))));
            properties.put("sessions", Property.of(n -> n.long_(fn -> fn.store(true).nullValue(-1L))));
            properties.put("greenInnovativeness", Property.of(n -> n.long_(fn -> fn.store(true).nullValue(-1L))));
            properties.put("interactionDynamics", Property.of(n -> n.float_(fn -> fn.store(true).nullValue(-1.0f))));
            properties.put("cost", Property.of(n -> n.float_(fn -> fn.store(true).nullValue(-1.0f))));
            properties.put("carbonFootprint", Property.of(n -> n.float_(fn -> fn.store(true).nullValue(-1.0f))));
            properties.put("sustainability", Property.of(n -> n.text(fn -> fn.store(true).fields("raw", Property.of(k -> k.keyword(fn2 -> fn2))))));
            properties.put("city", Property.of(n -> n.text(fn -> fn.store(true).fields("raw", Property.of(k -> k.keyword(fn2 -> fn2))))));
            properties.put("country", Property.of(n -> n.text(fn -> fn.store(true).fields("raw", Property.of(k -> k.keyword(fn2 -> fn2))))));
            properties.put("additionalMetric", Property.of(c -> c.long_(fn -> fn.store(true))));
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

    public HashSet<Integer> getAdditionalMetrics() {
        return additionalMetrics;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setEdition(int edition) {
        this.edition = edition;
    }

    public void setParticipants(int participants) {
        this.participants = participants;
    }

    public void setSessions(int sessions) {
        this.sessions = sessions;
    }

    public void setGreenInnovativeness(int greenInnovativeness) {
        this.greenInnovativeness = greenInnovativeness;
    }

    public void setInteractionDynamics(float interactionDynamics) {
        this.interactionDynamics = interactionDynamics;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public void setCarbonFootprint(float carbonFootprint) {
        this.carbonFootprint = carbonFootprint;
    }

    public void setSustainability(String sustainability) {
        this.sustainability = sustainability;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public static ConferenceEdition convertToConferenceEdition(ConferenceEditionDTO editionDTO) throws ExecutionException, InterruptedException {
        if (editionDTO == null) return null;
        ConferenceEdition conferenceEdition = new ConferenceEdition();
        conferenceEdition.setEdition(editionDTO.getEdition());
        conferenceEdition.setYear(editionDTO.getYear());
        conferenceEdition.setCarbonFootprint(editionDTO.getCarbonFootprint());
        conferenceEdition.setCity(editionDTO.getCity());
        conferenceEdition.setId(editionDTO.getId());
        conferenceEdition.setCost(editionDTO.getCost());
        conferenceEdition.setCountry(editionDTO.getCountry());
        conferenceEdition.setParticipants(editionDTO.getParticipants());
        conferenceEdition.setSessions(editionDTO.getSessions());
        conferenceEdition.setCarbonFootprint(editionDTO.getCarbonFootprint());
        conferenceEdition.setGreenInnovativeness(editionDTO.getGreenInnovativeness());
        conferenceEdition.setInteractionDynamics(editionDTO.getInteractionDynamics());
        for (int id : editionDTO.getAdditionalMetrics()) {
            conferenceEdition.getAdditionalMetrics().add(StorageController.getInstance().retrieveAdditionalMetric(id));
        }
        conferenceEdition.setSustainability(editionDTO.getSustainability());
        return conferenceEdition;
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

    @Override
    public String toString() {
        return "ConferenceEditionDTO{" +
                "id=" + id +
                ", year=" + year +
                ", edition=" + edition +
                ", participants=" + participants +
                ", sessions=" + sessions +
                ", greenInnovativeness=" + greenInnovativeness +
                ", interactionDynamics=" + interactionDynamics +
                ", cost=" + cost +
                ", carbonFootprint=" + carbonFootprint +
                ", sustainability='" + sustainability + '\'' +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", additionalMetrics=" + additionalMetrics +
                '}';
    }
}
