package masterthesis.conferences.server.controller.storage;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import masterthesis.conferences.ConferencesApplication;
import masterthesis.conferences.data.model.AdditionalMetric;
import masterthesis.conferences.data.model.Conference;
import masterthesis.conferences.data.model.ConferenceEdition;
import masterthesis.conferences.data.model.IngestConfiguration;
import masterthesis.conferences.data.model.dto.IngestConfigurationDTO;
import masterthesis.conferences.server.controller.Controller;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;

import java.util.*;
import java.util.concurrent.ExecutionException;

import static masterthesis.conferences.data.util.Indices.*;

public class StorageController implements Controller {
    private static StorageController instance = null;

    private static ElasticsearchAsyncClient client = null;

    private static final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

    private static MapperService mapperService = null;

    private static final Set<Conference> conferenceSet = new HashSet<>();

    public static StorageController getInstance() {
        if (instance == null) {
            instance = new StorageController();
            instance.fetchConferences();
        }
        return instance;
    }

    public static ElasticsearchAsyncClient getElasticInstance() {
        if (client == null) {
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials("elastic", "changeme"));
            RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200))
                    .setHttpClientConfigCallback(httpAsyncClientBuilder ->
                            httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider)).build();
            client = new ElasticsearchAsyncClient(new RestClientTransport(restClient, new JacksonJsonpMapper()));
        }
        return client;
    }

    public static MapperService getMapper() {
        if (mapperService == null) {
            mapperService = new MapperService();
        }
        return mapperService;
    }


    @Override
    public synchronized void init() throws ExecutionException, InterruptedException {
        ConferencesApplication.getLogger().info("Initializing Controller");
        getInstance();
        getMapper();
        ConferencesApplication.getLogger().info("Controller Objects initialized");
        ConferencesApplication.getLogger().info("Initializing Elasticsearch Index");
        boolean indexCreated;
        if (ConferencesApplication.DEBUG) {
            if (ElasticReadOperation.existsIndex(CONFERENCE.index())) {
                ElasticWriteOperation.deleteIndex(CONFERENCE.index());
            }
            if (ElasticReadOperation.existsIndex(CONFERENCE_EDITION.index())) {
                ElasticWriteOperation.deleteIndex(CONFERENCE_EDITION.index());
            }
            if (ElasticReadOperation.existsIndex(ADDITIONAL_METRIC.index())) {
                ElasticWriteOperation.deleteIndex(ADDITIONAL_METRIC.index());
            }
            if (ElasticReadOperation.existsIndex(INGEST_CONFIGURATION.index())) {
                ElasticWriteOperation.deleteIndex(INGEST_CONFIGURATION.index());
            }

        }

        indexCreated = ElasticReadOperation.existsIndex(CONFERENCE.index());
        indexCreated &= ElasticReadOperation.existsIndex(CONFERENCE_EDITION.index());
        indexCreated &= ElasticReadOperation.existsIndex(ADDITIONAL_METRIC.index());
        indexCreated &= ElasticReadOperation.existsIndex(INGEST_CONFIGURATION.index());
        if (!indexCreated) {
            initIndex(CONFERENCE.index());
            initIndex(CONFERENCE_EDITION.index());
            initIndex(ADDITIONAL_METRIC.index());
            initIndex(INGEST_CONFIGURATION.index());
        } else {
            ElasticWriteOperation.openIndex(CONFERENCE.index());
            ElasticWriteOperation.openIndex(CONFERENCE_EDITION.index());
            ElasticWriteOperation.openIndex(ADDITIONAL_METRIC.index());
            ElasticWriteOperation.openIndex(INGEST_CONFIGURATION.index());
        }
        ConferencesApplication.getLogger().info("Elasticsearch Index initialized");
    }

    @Override
    public void shutdown() throws InterruptedException {
        ElasticWriteOperation.closeIndex(CONFERENCE.index());
        ElasticWriteOperation.closeIndex(CONFERENCE_EDITION.index());
        ElasticWriteOperation.closeIndex(ADDITIONAL_METRIC.index());
        ElasticWriteOperation.closeIndex(INGEST_CONFIGURATION.index());
    }

    private void initIndex(String indexName) throws InterruptedException {
        ElasticWriteOperation.createIndex(indexName);
        ElasticWriteOperation.createMapping(indexName);
    }

    public Conference getConference(String title) {
        return conferenceSet.stream().filter(c -> c.getTitle().equals(title)).findFirst().orElse(null);
    }

    public List<Conference> getConferences() {
        return new ArrayList<>(conferenceSet);
    }

    public void save(Conference conference) throws InterruptedException {
        conferenceSet.remove(conference);
        conferenceSet.add(conference);
        indexConference(conference);
    }

    public void deleteById(String title) throws InterruptedException {
        removeConference(getConference(title));
        conferenceSet.remove(getConference(title));
    }

    public Optional<Conference> findById(String title) {
        Conference conference = getConference(title);
        if (conference == null) return Optional.empty();
        return Optional.of(conference);
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

    public void indexConference(Conference conference) throws InterruptedException {
        conferenceSet.add(conference);
        ElasticWriteOperation.writeConference(
                Objects.requireNonNull(getMapper()).convertToConferenceDTO(conference.getTitle()), CONFERENCE.index()
        );
    }

    public void indexConferenceEdition(ConferenceEdition edition, Conference conference) throws InterruptedException {
        conference.addConferenceEdition(edition);
        conferenceSet.remove(conference);
        conferenceSet.add(conference);
        ElasticWriteOperation.writeConferenceEdition(
                Objects.requireNonNull(getMapper()).convertToConferenceEditionDTO(edition.getId())
        );
        indexConference(conference);
    }

    public void indexAdditionalMetric(ConferenceEdition edition, Conference conference, AdditionalMetric metric) throws InterruptedException {
        edition.addAdditionalMetric(metric);
        edition.updateAdditionalMetric(metric);
        ElasticWriteOperation.writeAdditionalMetric(
                Objects.requireNonNull(getMapper()).convertToAdditionalMetricDTO(metric.getId())
        );
        indexConferenceEdition(edition, conference);
    }

    public void indexIngestConfiguration(ConferenceEdition edition, Conference conference, AdditionalMetric metric, IngestConfiguration config) throws InterruptedException {
        metric.setConfig(config);
        IngestConfigurationDTO ingestDTO = Objects.requireNonNull(getMapper()).convertToIngestConfigurationDTO(config.getId());
        if (ingestDTO == null) return;
        ElasticWriteOperation.writeIngestConfiguration(ingestDTO);
        indexAdditionalMetric(edition, conference, metric);
    }

    public void removeConference(Conference conference) throws InterruptedException {
        if (conference.getConferenceEditions() != null) {
            for (ConferenceEdition edition : conference.getConferenceEditions()) {
                removeConferenceEdition(edition);
            }
        }
        ElasticWriteOperation.deleteConference(conference.getTitle());
        conferenceSet.remove(conference);
    }

    public void removeConferenceEdition(ConferenceEdition edition) throws InterruptedException {
        if (edition.getAdditionalMetrics() != null) {
            for (AdditionalMetric metric : edition.getAdditionalMetrics()) {
                removeAdditionalMetric(metric);
            }
        }
        ElasticWriteOperation.deleteConferenceEdition(edition.getId());
        final int id = edition.getId();
        Conference conferenceToRemove = null;
        ConferenceEdition editionToRemove = null;
        for (Conference c : conferenceSet) {
            if (c.getConferenceEditionIds().contains(id)) {
                for (ConferenceEdition conferenceEdition : c.getConferenceEditions()) {
                    if (edition.getId() == id) {
                        editionToRemove = conferenceEdition;
                        conferenceToRemove = c;
                    }
                }
            }
        }
        if (editionToRemove != null) {
            conferenceToRemove.getConferenceEditions().remove(editionToRemove);
        }
    }

    public void removeAdditionalMetric(AdditionalMetric metric) throws InterruptedException {
        removeIngestConfiguration(metric.getConfig());
        ElasticWriteOperation.deleteAdditionalMetric(metric.getId());
        final int id = metric.getId();
        ConferenceEdition editionToRemove = null;
        AdditionalMetric metricToRemove = null;
        for (Conference c : conferenceSet) {
            for (ConferenceEdition edition : c.getConferenceEditions()) {
                if (edition.getAdditionalMetricIds().contains(id)) {
                    for (AdditionalMetric additionalMetric : edition.getAdditionalMetrics()) {
                        if (metric.getId() == id) {
                            editionToRemove = edition;
                            metricToRemove = additionalMetric;
                        }
                    }
                }
            }
        }
        if (editionToRemove != null) {
            editionToRemove.getAdditionalMetrics().remove(metricToRemove);
        }
    }

    public void removeIngestConfiguration(IngestConfiguration config) throws InterruptedException {
        if (config == null) return;
        ElasticWriteOperation.deleteIngestConfiguration(config.getId());
        final int id = config.getId();
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


    public void fetchConferences() {
        List<Conference> conferenceList = new ArrayList<>();
        try {
            conferenceList = ElasticReadOperation.retrieveConferences();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        conferenceSet.addAll(conferenceList);
    }


}
