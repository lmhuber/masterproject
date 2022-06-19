package masterthesis.conferences.data;

import masterthesis.conferences.data.model.Conference;
import masterthesis.conferences.data.model.ConferenceEdition;
import masterthesis.conferences.server.rest.storage.StorageController;

import java.util.*;

public class ConferenceRepository {
    private final Set<Conference> conferenceSet;

    public ConferenceRepository() {
        conferenceSet = new HashSet<>();
    }

    public void addConference(Conference conference) {
        this.conferenceSet.add(conference);
    }

    public Set<Conference> getConferenceSet() {
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

    public List<Conference> getConferences() {
        return new ArrayList<>(conferenceSet);
    }

    public void save(Conference conference) throws InterruptedException {
        this.addConference(conference);
        StorageController.getControllerInstance().indexConference(conference);
    }

    public void deleteById(String title) {
        conferenceSet.remove(getConference(title));
    }

    public Optional<Conference> findById(String title) {
        return Optional.of(getConference(title));
    }
}
