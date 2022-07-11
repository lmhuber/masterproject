package masterthesis.conferences.server.controller;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import masterthesis.conferences.ConferencesApplication;
import masterthesis.conferences.data.ConferenceRepository;
import masterthesis.conferences.data.MapperService;
import masterthesis.conferences.data.model.AdditionalMetric;
import masterthesis.conferences.data.model.Conference;
import masterthesis.conferences.data.model.ConferenceEdition;
import masterthesis.conferences.server.rest.storage.ElasticDeleteOperations;
import masterthesis.conferences.server.rest.storage.ElasticIndexOperations;
import masterthesis.conferences.server.rest.storage.ElasticSearchOperations;
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

    protected ElasticsearchAsyncClient getInstance() {
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

    protected MapperService getMapper() {
        if (mapperService == null) {
            if (repository == null) return null;
            mapperService = new MapperService();
        }
        return mapperService;
    }

    protected ConferenceRepository getRepository() {
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
            if (ElasticSearchOperations.existsIndex(CONFERENCE.indexName())) {
                ElasticDeleteOperations.deleteIndex(CONFERENCE.indexName());
            }
            if (ElasticSearchOperations.existsIndex(CONFERENCE_EDITION.indexName())) {
                ElasticDeleteOperations.deleteIndex(CONFERENCE_EDITION.indexName());
            }
        }

        indexCreated = ElasticSearchOperations.existsIndex(CONFERENCE.indexName());
        indexCreated &= ElasticSearchOperations.existsIndex(CONFERENCE_EDITION.indexName());
        if (!indexCreated) {
            initIndex(CONFERENCE.indexName());
            initIndex(CONFERENCE_EDITION.indexName());
            initIndex(ADDITIONAL_METRIC.indexName());
        } else {
            ElasticIndexOperations.openIndex(CONFERENCE.indexName());
            ElasticIndexOperations.openIndex(CONFERENCE_EDITION.indexName());
            ElasticIndexOperations.openIndex(ADDITIONAL_METRIC.indexName());
        }
        ConferencesApplication.getLogger().info("Elasticsearch Index initialized");
    }

    @Override
    public void shutdown() throws InterruptedException {
        ElasticIndexOperations.closeIndex(CONFERENCE.indexName());
        ElasticIndexOperations.closeIndex(CONFERENCE_EDITION.indexName());
        ElasticIndexOperations.closeIndex(ADDITIONAL_METRIC.indexName());
    }

    private void initIndex(String indexName) throws InterruptedException {
        ElasticIndexOperations.createIndex(indexName);
        ElasticIndexOperations.createMapping(indexName);
    }

    protected void indexConference(Conference conference) throws InterruptedException {
        repository.addConference(conference);
        ElasticIndexOperations.writeConference(
                Objects.requireNonNull(getMapper()).convertToConferenceDTO(conference.getTitle()), CONFERENCE.indexName()
        );
    }

    protected void indexConferenceEdition(ConferenceEdition edition, Conference conference) throws InterruptedException {
        conference.addConferenceEdition(edition);
        repository.updateConference(conference);
        ElasticIndexOperations.writeConferenceEdition(
                Objects.requireNonNull(getMapper()).convertToConferenceEditionDTO(edition.getId())
        );
        indexConference(conference);
    }

    protected void indexAdditionalMetric(ConferenceEdition edition, Conference conference, AdditionalMetric metric) throws InterruptedException {
        edition.addAdditionalMetric(metric);
        ElasticIndexOperations.writeAdditionalMetric(
                Objects.requireNonNull(getMapper()).convertToAdditionalMetricDTO(metric.getId())
        );
        indexConferenceEdition(edition, conference);
    }

    protected void removeConference(Conference conference) throws InterruptedException {
        if (conference.getConferenceEditions() != null) {
            for (ConferenceEdition edition : conference.getConferenceEditions()) {
                ElasticDeleteOperations.deleteConferenceEdition(edition.getId());
            }
        }
        ElasticDeleteOperations.deleteConference(conference.getTitle());
        repository.deleteConference(conference);
    }

    protected void removeConferenceEdition(ConferenceEdition edition) throws InterruptedException {
        if (edition.getAdditionalMetrics() != null) {
            for (AdditionalMetric metric : edition.getAdditionalMetrics()) {
                ElasticDeleteOperations.deleteAdditionalMetric(metric.getId());
            }
        }
        ElasticDeleteOperations.deleteConferenceEdition(edition.getId());
        repository.removeEdition(edition.getId());
    }

    public void removeAdditionalMetric(AdditionalMetric metric) throws InterruptedException {
        ElasticDeleteOperations.deleteAdditionalMetric(metric.getId());
        repository.removeAdditionalMetric(metric.getId());
    }


    protected void fetchConferences() {
        List<Conference> conferenceList = new ArrayList<>();
        try {
            conferenceList = ElasticSearchOperations.retrieveConferences();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        for (Conference c : conferenceList) repository.addConference(c);
    }

}
