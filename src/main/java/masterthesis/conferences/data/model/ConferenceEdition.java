package masterthesis.conferences.data.model;

import masterthesis.conferences.server.controller.storage.rest.ElasticCountOperations;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class ConferenceEdition {
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

    private final HashSet<AdditionalMetric> additionalMetrics = new HashSet<>();

    public ConferenceEdition() {

    }

    public ConferenceEdition(int year, int edition, int participants, int sessions,
                             int greenInnovativeness, float interactionDynamics, float cost,
                             float carbonFootprint, String sustainability, String country,
                             String city, AdditionalMetric... additionalMetrics) throws ExecutionException, InterruptedException {
        this.id = ElasticCountOperations.getMaxConferenceEditionId();
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
        this.additionalMetrics.addAll(Set.of(additionalMetrics));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getEdition() {
        return edition;
    }

    public void setEdition(int edition) {
        this.edition = edition;
    }

    public int getParticipants() {
        return participants;
    }

    public void setParticipants(int participants) {
        this.participants = participants;
    }

    public int getSessions() {
        return sessions;
    }

    public void setSessions(int sessions) {
        this.sessions = sessions;
    }

    public int getGreenInnovativeness() {
        return greenInnovativeness;
    }

    public void setGreenInnovativeness(int greenInnovativeness) {
        this.greenInnovativeness = greenInnovativeness;
    }

    public float getInteractionDynamics() {
        return interactionDynamics;
    }

    public void setInteractionDynamics(float interactionDynamics) {
        this.interactionDynamics = interactionDynamics;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public float getCarbonFootprint() {
        return carbonFootprint;
    }

    public void setCarbonFootprint(float carbonFootprint) {
        this.carbonFootprint = carbonFootprint;
    }

    public String getSustainability() {
        return sustainability;
    }

    public void setSustainability(String sustainability) {
        this.sustainability = sustainability;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public HashSet<AdditionalMetric> getAdditionalMetrics() {
        return additionalMetrics;
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

    @Override
    public String toString() {
        return "ConferenceEdition{" +
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
