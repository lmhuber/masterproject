package masterthesis.conferences.data.dto;

import java.util.HashMap;

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
}
