package masterthesis.conferences.data;

import masterthesis.conferences.data.model.Conference;
import masterthesis.conferences.data.model.ConferenceEdition;

import java.util.HashSet;
import java.util.Set;

public class ConferenceRepository {
    private final Set<Conference> conferenceSet;

    public ConferenceRepository() {
        conferenceSet = new HashSet<>();
    }

    public void addConference(Conference conference) {
        this.conferenceSet.add(conference);
    }

    public Set<Conference> getConferences() {
        return conferenceSet;
    }

    public void updateConference(Conference conference) {
        deleteConference(conference);
        this.conferenceSet.add(conference);
    }

    public void deleteConference(Conference conference) {
        this.conferenceSet.remove(conference);
    }

    public Conference getConference(String title) {
        return conferenceSet.stream().filter(c -> c.getTitle().equals(title)).findFirst().orElse(null);
    }

    public ConferenceEdition getEdition(int id) {
        for (Conference c : conferenceSet) {
            if (c.getConferenceEditionIds().contains(id)) {
                for (ConferenceEdition edition : c.getConferenceEditions()) {
                    if (edition.getId() == id) return edition;
                }
            }
        }
        return null;
    }
}
