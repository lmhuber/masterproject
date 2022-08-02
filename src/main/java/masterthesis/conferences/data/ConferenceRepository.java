package masterthesis.conferences.data;

import masterthesis.conferences.data.model.AdditionalMetric;
import masterthesis.conferences.data.model.Conference;
import masterthesis.conferences.data.model.ConferenceEdition;
import masterthesis.conferences.data.model.IngestConfiguration;
import masterthesis.conferences.server.controller.StorageController;
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
            StorageController.indexConferenceEdition(editionToAdd, conferenceToAdd);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void addMetric(Conference conferenceToAdd, ConferenceEdition editionToAdd, AdditionalMetric metricToAdd) {
        if (metricToAdd == null) return;
        try {
            StorageController.indexAdditionalMetric(editionToAdd, conferenceToAdd, metricToAdd);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void addConfig(Conference conferenceToAdd, ConferenceEdition editionToAdd, AdditionalMetric metricToAdd, IngestConfiguration config) {
        if (config == null) return;
        try {
            StorageController.indexIngestConfiguration(editionToAdd, conferenceToAdd, metricToAdd, config);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void removeAdditionalMetric(int id) {
        ConferenceEdition editionToRemove = null;
        AdditionalMetric metricToRemove = null;
        for (Conference c : conferenceSet) {
            for (ConferenceEdition edition : c.getConferenceEditions()) {
                if (edition.getAdditionalMetricIds().contains(id)) {
                    for (AdditionalMetric metric : edition.getAdditionalMetrics()) {
                        if (metric.getId() == id) {
                            editionToRemove = edition;
                            metricToRemove = metric;
                        }
                    }
                }
            }
        }
        if (editionToRemove != null) {
            editionToRemove.getAdditionalMetrics().remove(metricToRemove);
        }
    }

    public void removeIngestConfiguration(int id) {
        AdditionalMetric metricToRemove = null;
        for (Conference c : conferenceSet) {
            for (ConferenceEdition edition : c.getConferenceEditions()) {
                if (edition.getAdditionalMetricIds().contains(id)) {
                    for (AdditionalMetric metric : edition.getAdditionalMetrics()) {
                        if (metric.getConfig().getId() == id) {
                            metricToRemove = metric;
                        }
                    }
                }
            }
        }
        if (metricToRemove != null) {
            metricToRemove.setConfig(null);
        }
    }



    public List<Conference> getConferences() {
        return new ArrayList<>(conferenceSet);
    }

    public void save(Conference conference) throws InterruptedException {
        this.updateConference(conference);
        StorageController.indexConference(conference);
    }

    public void deleteById(String title) throws InterruptedException {
        StorageController.removeConference(getConference(title));
        conferenceSet.remove(getConference(title));
    }

    public Optional<Conference> findById(String title) {
        Conference conference = getConference(title);
        if (conference == null) return Optional.empty();
        return Optional.of(conference);
    }

    public AdditionalMetric getMetric(int id) {
        for (Conference c : conferenceSet) {
            for (ConferenceEdition e : c.getConferenceEditions()) {
                for (AdditionalMetric metric : e.getAdditionalMetrics()) {
                    if (metric.getId() == id) return metric;
                }
            }
        }
        return null;
    }
    
    public ConferenceEdition getEditonByMetric(int id) {
        for (Conference c : conferenceSet) {
            for (ConferenceEdition edition : c.getConferenceEditions()) {
                if (edition.getAdditionalMetricIds().contains(id)) {
                    return edition;
                }
            }
        }
        return null;
    }

    public IngestConfiguration getIngestConfiguration(int ingestConfigId) {
        for (Conference c : conferenceSet) {
            for (ConferenceEdition edition : c.getConferenceEditions()) {
                for (AdditionalMetric metric : edition.getAdditionalMetrics()) {
                    if (metric.getConfig() != null && metric.getConfig().getId() == ingestConfigId) {
                        return metric.getConfig();
                    }
                }
            }
        }
        return null;
    }

}
