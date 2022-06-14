package masterthesis.conferences.data.model;

import java.util.HashSet;
import java.util.Set;

public class Conference {
    private final String title;
    private final String organization;
    private final String publisher;
    private final HashSet<ConferenceEdition> conferenceEditions;


    public Conference(String title, String organization, String publisher, ConferenceEdition... editions) {
        this.title = title;
        this.organization = organization;
        this.publisher = publisher;
        this.conferenceEditions = new HashSet<>();
        this.conferenceEditions.addAll(Set.of(editions));
    }



}
