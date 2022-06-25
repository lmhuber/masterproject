package masterthesis.conferences.data.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Conference {
    private String title;
    private String organization;
    private String publisher;
    private final HashSet<ConferenceEdition> conferenceEditions = new HashSet<>();


    public Conference(String title, String organization, String publisher, ConferenceEdition... editions) {
        this.title = title;
        this.organization = organization;
        this.publisher = publisher;
        this.conferenceEditions.addAll(Set.of(editions));
    }

    public Conference() {

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

    public HashSet<ConferenceEdition> getConferenceEditions() {
        return conferenceEditions;
    }

    public void addConferenceEdition(ConferenceEdition edition) {
        this.conferenceEditions.add(edition);
    }

    public void updateConferenceEdition(ConferenceEdition edition) {
        this.conferenceEditions.remove(edition);
        this.conferenceEditions.add(edition);
    }

    public Set<Integer> getConferenceEditionIds() {
        return conferenceEditions.stream().map(ConferenceEdition::getId).collect(Collectors.toSet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conference that = (Conference) o;
        return Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }

    @Override
    public String toString() {
        return "Conference{" +
                "title='" + title + '\'' +
                ", organization='" + organization + '\'' +
                ", publisher='" + publisher + '\'' +
                ", conferenceEditions=" + conferenceEditions +
                '}';
    }

    public void setConferenceEditions(HashSet<ConferenceEdition> conferenceEditions) {
        if (conferenceEditions == null) return;
        this.conferenceEditions.clear();
        this.conferenceEditions.addAll(conferenceEditions);
    }
}
