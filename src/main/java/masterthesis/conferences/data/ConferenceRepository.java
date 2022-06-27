package masterthesis.conferences.data;

import masterthesis.conferences.data.model.Conference;
import masterthesis.conferences.data.model.ConferenceEdition;
import masterthesis.conferences.server.rest.storage.StorageController;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
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
            if (c.getConferenceEditionIds()!= null && c.getConferenceEditionIds().contains(id)) {
                for (ConferenceEdition edition : c.getConferenceEditions()) {
                    if (edition.getId() == id) return edition;
                }
            }
        }
        return null;
    }

    public void removeEdition(int id) {
        Conference conferenceToRemove = null;
        ConferenceEdition editionToRemove = null;
        for (Conference c : conferenceSet) {
            if (c.getConferenceEditionIds().contains(id)) {
                for (ConferenceEdition edition : c.getConferenceEditions()) {
                    if (edition.getId() == id) {
                        editionToRemove = edition;
                        conferenceToRemove = c;
                    }
                }
            }
        }
        if (editionToRemove != null) {
            conferenceToRemove.getConferenceEditions().remove(editionToRemove);
        }
    }

    public void addEdition(Conference conferenceToAdd, ConferenceEdition editionToAdd) {
        if (editionToAdd == null) return;
        try {
            StorageController.getControllerInstance().indexConferenceEdition(editionToAdd, conferenceToAdd);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public List<Conference> getConferences() {
        return new ArrayList<>(conferenceSet);
    }

    public void save(Conference conference) throws InterruptedException {
        this.addConference(conference);
        StorageController.getControllerInstance().indexConference(conference);
    }

    public void deleteById(String title) throws InterruptedException {
        StorageController.getControllerInstance().removeConference(getConference(title));
        conferenceSet.remove(getConference(title));
    }

    public Optional<Conference> findById(String title) {
        return Optional.of(getConference(title));
    }
}
