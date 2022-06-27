package masterthesis.conferences.data.dto;

import co.elastic.clients.elasticsearch._types.mapping.Property;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ConferenceDTO {
    private String title;
    private String organization;
    private String publisher;
    private Set<Integer> conferenceEdtions;

    private final static Map<String, Property> properties = new HashMap<>();

    public ConferenceDTO() {

    }

    public ConferenceDTO(String title, String organization, String publisher, Set<Integer> conferenceEditions) {
        this.title = title;
        this.organization = organization;
        this.publisher = publisher;
        this.conferenceEdtions = conferenceEditions;
    }

    public String getTitle() {
        return title;
    }

    public String getOrganization() {
        return organization;
    }

    public String getPublisher() {
        return publisher;
    }

    public Set<Integer> getConferenceEdtions() {
        return conferenceEdtions;
    }

    public static Map<String, Property> getProperties() {
        if (properties.isEmpty()) {
            properties.put("title", Property.of(c -> c.text(fn -> fn.store(true).fields("raw", Property.of(k -> k.keyword(fn2 -> fn2))))));
            properties.put("organization", Property.of(c -> c.text(fn -> fn.store(true).fields("raw", Property.of(k -> k.keyword(fn2 -> fn2))))));
            properties.put("publisher", Property.of(c -> c.text(fn -> fn.store(true).fields("raw", Property.of(k -> k.keyword(fn2 -> fn2))))));
            properties.put("conferenceEditions", Property.of(c -> c.long_(fn -> fn.store(true))));
        }
        return properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConferenceDTO that = (ConferenceDTO) o;
        return Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }

    @Override
    public String toString() {
        return "ConferenceDTO{" +
                "title='" + title + '\'' +
                ", organization='" + organization + '\'' +
                ", publisher='" + publisher + '\'' +
                ", conferenceEdtions=" + conferenceEdtions +
                '}';
    }
}
