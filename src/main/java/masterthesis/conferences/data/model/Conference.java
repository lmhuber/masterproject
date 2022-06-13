package masterthesis.conferences.data.model;

import co.elastic.clients.elasticsearch._types.mapping.IntegerNumberProperty;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.mapping.TextProperty;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Conference {
    private final String title;
    private final String organization;
    private final String publisher;
    private final HashSet<ConferenceEdition> conferenceEditions;

    private final static Map<String, Property> properties = new HashMap<>();

    public Conference(String title, String organization, String publisher, ConferenceEdition... editions) {
        this.title = title;
        this.organization = organization;
        this.publisher = publisher;
        this.conferenceEditions = new HashSet<>();
        this.conferenceEditions.addAll(Set.of(editions));
    }

    public static Map<String, Property> getProperties() {
        if (properties.isEmpty()) {
            properties.put("title", new Property(TextProperty.of(t -> t)));
            properties.put("organization", new Property(TextProperty.of(t -> t)));
            properties.put("publisher", new Property(TextProperty.of(t -> t)));
            properties.put("conferenceEditions", new Property(IntegerNumberProperty.of(n -> n)));
        }
        return properties;
    }


}
