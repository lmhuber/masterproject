package masterthesis.conferences.data.dto;

import co.elastic.clients.elasticsearch._types.mapping.Property;

import java.util.*;

public class ConferenceDTO {
    private final String title;
    private final String organization;
    private final String publisher;
    private final HashSet<Integer> conferenceEdtions;
    private final HashSet<ConferenceEditionDTO> conferenceEditionDTOs;

    private final static Map<String, Property> properties = new HashMap<>();

    public ConferenceDTO(String title, String organization, String publisher, ConferenceEditionDTO... conferenceEditionDTOs) {
        this.title = title;
        this.organization = organization;
        this.publisher = publisher;
        this.conferenceEdtions = new HashSet<>();
        this.conferenceEditionDTOs = new HashSet<>();
        this.conferenceEditionDTOs.addAll(List.of(conferenceEditionDTOs));
        Arrays.stream(conferenceEditionDTOs).forEach(d -> conferenceEdtions.add(d.getId()));
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

    public HashSet<Integer> getConferenceEdtions() {
        return conferenceEdtions;
    }

    public static Map<String, Property> getProperties() {
        if (properties.isEmpty()) {
            properties.put("title", Property.of(c -> c.text(fn -> fn.store(true))));
            properties.put("organization", Property.of(c -> c.text(fn -> fn.store(true))));
            properties.put("publisher", Property.of(c -> c.text(fn -> fn.store(true))));
            properties.put("conferenceEditions", Property.of(c -> c.integer(fn -> fn.store(true))));
        }
        return properties;
    }

}
