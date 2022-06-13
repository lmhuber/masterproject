package masterthesis.conferences.data.dto;

import java.util.HashSet;
import java.util.List;

public class ConferenceDTO {
    private final String title;
    private final String organization;
    private final String publisher;
    private final HashSet<ConferenceEditionDTO> conferenceEditions;

    public ConferenceDTO(String title, String organization, String publisher, ConferenceEditionDTO... editions){
        this.title = title;
        this.organization = organization;
        this.publisher = publisher;
        this.conferenceEditions = new HashSet<>();
        this.conferenceEditions.addAll(List.of(editions));
    }


}
