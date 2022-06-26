package masterthesis.conferences.data.dto;

import java.util.Objects;

public class ConferenceFrontendDTO {
    private int id;
    private int year;
    private int edition;
    private int participants;
    private int sessions;

    private String country;
    private String city;
    private String title;
    private String organization;
    private String publisher;

    public ConferenceFrontendDTO() {

    }

    public ConferenceFrontendDTO(int id, int year, int edition, int participants,
                                 int sessions, String country, String city, String title,
                                 String organization, String publisher) {
        this.id = id;
        this.year = year;
        this.edition = edition;
        this.participants = participants;
        this.sessions = sessions;
        this.country = country;
        this.city = city;
        this.title = title;
        this.organization = organization;
        this.publisher = publisher;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConferenceFrontendDTO that = (ConferenceFrontendDTO) o;
        return id == that.id && year == that.year && edition == that.edition
                && participants == that.participants && sessions == that.sessions &&
                Objects.equals(country, that.country) && Objects.equals(city, that.city) &&
                Objects.equals(title, that.title) && Objects.equals(organization, that.organization)
                && Objects.equals(publisher, that.publisher);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, year, edition, participants, sessions,
                country, city, title, organization, publisher);
    }
}
