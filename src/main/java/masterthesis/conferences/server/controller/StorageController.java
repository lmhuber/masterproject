package masterthesis.conferences.server.controller;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import masterthesis.conferences.ConferencesApplication;
import masterthesis.conferences.data.ConferenceRepository;
import masterthesis.conferences.data.MapperService;
import masterthesis.conferences.data.dto.IngestConfigurationDTO;
import masterthesis.conferences.data.model.AdditionalMetric;
import masterthesis.conferences.data.model.Conference;
import masterthesis.conferences.data.model.ConferenceEdition;
import masterthesis.conferences.data.model.IngestConfiguration;
import masterthesis.conferences.server.rest.storage.ElasticReadOperation;
import masterthesis.conferences.server.rest.storage.ElasticWriteOperation;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static masterthesis.conferences.data.util.Indices.*;

public class StorageController implements Controller {
    private static ElasticsearchAsyncClient client = null;

    private static final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

    private static ConferenceRepository repository = null;

    private static MapperService mapperService = null;

    public static ElasticsearchAsyncClient getInstance() {
        if (client == null) {
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials("elastic", "changeme"));
            RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200))
                    .setHttpClientConfigCallback(httpAsyncClientBuilder ->
                            httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider)).build();
            ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
            client = new ElasticsearchAsyncClient(transport);
        }
        return client;
    }

    public static MapperService getMapper() {
        if (mapperService == null) {
            if (repository == null) return null;
            mapperService = new MapperService();
        }
        return mapperService;
    }

    public static ConferenceRepository getRepository() {
        if (repository == null) {
            repository = new ConferenceRepository();
            fetchConferences();
        }
        return repository;
    }

    @Override
    public synchronized void init() throws ExecutionException, InterruptedException {
        ConferencesApplication.getLogger().info("Initializing Controller");
        getInstance();
        getMapper();
        getRepository();
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
        }

        indexCreated = ElasticReadOperation.existsIndex(CONFERENCE.index());
        indexCreated &= ElasticReadOperation.existsIndex(CONFERENCE_EDITION.index());
        if (!indexCreated) {
            initIndex(CONFERENCE.index());
            initIndex(CONFERENCE_EDITION.index());
            initIndex(ADDITIONAL_METRIC.index());
        } else {
            ElasticWriteOperation.openIndex(CONFERENCE.index());
            ElasticWriteOperation.openIndex(CONFERENCE_EDITION.index());
            ElasticWriteOperation.openIndex(ADDITIONAL_METRIC.index());
        }
        ConferencesApplication.getLogger().info("Elasticsearch Index initialized");
    }

    @Override
    public void shutdown() throws InterruptedException {
        ElasticWriteOperation.closeIndex(CONFERENCE.index());
        ElasticWriteOperation.closeIndex(CONFERENCE_EDITION.index());
        ElasticWriteOperation.closeIndex(ADDITIONAL_METRIC.index());
    }

    private void initIndex(String indexName) throws InterruptedException {
        ElasticWriteOperation.createIndex(indexName);
        ElasticWriteOperation.createMapping(indexName);
    }

    public static void indexConference(Conference conference) throws InterruptedException {
        repository.addConference(conference);
        ElasticWriteOperation.writeConference(
                Objects.requireNonNull(getMapper()).convertToConferenceDTO(conference.getTitle()), CONFERENCE.index()
        );
    }

    public static void indexConferenceEdition(ConferenceEdition edition, Conference conference) throws InterruptedException {
        conference.addConferenceEdition(edition);
        repository.updateConference(conference);
        ElasticWriteOperation.writeConferenceEdition(
                Objects.requireNonNull(getMapper()).convertToConferenceEditionDTO(edition.getId())
        );
        indexConference(conference);
    }

    public static void indexAdditionalMetric(ConferenceEdition edition, Conference conference, AdditionalMetric metric) throws InterruptedException {
        edition.addAdditionalMetric(metric);
        ElasticWriteOperation.writeAdditionalMetric(
                Objects.requireNonNull(getMapper()).convertToAdditionalMetricDTO(metric.getId())
        );
        indexConferenceEdition(edition, conference);
    }

    public static void indexIngestConfiguration(ConferenceEdition edition, Conference conference, AdditionalMetric metric, IngestConfiguration config) throws InterruptedException {
        metric.setConfig(config);
        IngestConfigurationDTO ingestDTO = Objects.requireNonNull(getMapper()).convertToIngestConfigurationDTO(config.getId());
        if (ingestDTO == null) return;
        ElasticWriteOperation.writeIngestConfiguration(ingestDTO);
        indexAdditionalMetric(edition, conference, metric);
    }

    public static void removeConference(Conference conference) throws InterruptedException {
        if (conference.getConferenceEditions() != null) {
            for (ConferenceEdition edition : conference.getConferenceEditions()) {
                ElasticWriteOperation.deleteConferenceEdition(edition.getId());
            }
        }
        ElasticWriteOperation.deleteConference(conference.getTitle());
        repository.deleteConference(conference);
    }

    public static void removeConferenceEdition(ConferenceEdition edition) throws InterruptedException {
        if (edition.getAdditionalMetrics() != null) {
            for (AdditionalMetric metric : edition.getAdditionalMetrics()) {
                ElasticWriteOperation.deleteAdditionalMetric(metric.getId());
            }
        }
        ElasticWriteOperation.deleteConferenceEdition(edition.getId());
        repository.removeEdition(edition.getId());
    }

    public static void removeAdditionalMetric(AdditionalMetric metric) throws InterruptedException {
        ElasticWriteOperation.deleteAdditionalMetric(metric.getId());
        repository.removeAdditionalMetric(metric.getId());
    }

    public static void removeIngestConfiguration(IngestConfiguration config) throws InterruptedException {
        ElasticWriteOperation.deleteIngestConfiguration(config.getId());
        repository.removeIngestConfiguration(config.getId());
    }


    public static void fetchConferences() {
        List<Conference> conferenceList = new ArrayList<>();
        try {
            conferenceList = ElasticReadOperation.retrieveConferences();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        for (Conference c : conferenceList) repository.addConference(c);
    }

}
