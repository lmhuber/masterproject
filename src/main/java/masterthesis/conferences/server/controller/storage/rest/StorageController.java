package masterthesis.conferences.server.controller.storage.rest;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import masterthesis.conferences.ConferencesApplication;
import masterthesis.conferences.data.ConferenceRepository;
import masterthesis.conferences.data.MapperService;
import masterthesis.conferences.data.model.Conference;
import masterthesis.conferences.data.model.ConferenceEdition;
import masterthesis.conferences.server.controller.Controller;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static masterthesis.conferences.data.util.Indices.CONFERENCE;
import static masterthesis.conferences.data.util.Indices.CONFERENCE_EDITION;

public class StorageController implements Controller {
    private static ElasticsearchAsyncClient client = null;

    private static final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

    private static ConferenceRepository repository = null;

    private static MapperService mapperService = null;

    protected static ElasticsearchAsyncClient getInstance() {
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
            mapperService = new MapperService(repository);
        }
        return mapperService;
    }

    public static ConferenceRepository getRepository() {
        if (repository == null) {
            repository = new ConferenceRepository();
        }
        return repository;
    }

    @Override
    public synchronized void init() throws ExecutionException, InterruptedException {
        ConferencesApplication.getLogger().info("Initializing Elasticsearch Index");
        boolean indexCreated;
        if (ConferencesApplication.DEBUG) {
            ElasticIndexOperations.deleteIndex(CONFERENCE.indexName());
            ElasticIndexOperations.deleteIndex(CONFERENCE_EDITION.indexName());
        }

        indexCreated = ElasticSearchOperations.existsIndex(CONFERENCE.indexName());
        indexCreated &= ElasticSearchOperations.existsIndex(CONFERENCE_EDITION.indexName());
        if (!indexCreated) {
            initIndex(CONFERENCE.indexName());
            initIndex(CONFERENCE_EDITION.indexName());
        } else {
            ElasticIndexOperations.openIndex(CONFERENCE.indexName());
            ElasticIndexOperations.openIndex(CONFERENCE_EDITION.indexName());
        }
        ConferencesApplication.getLogger().info("Elasticsearch Index initialized");
    }

    @Override
    public void shutdown() throws InterruptedException {
        ElasticIndexOperations.closeIndex(CONFERENCE.indexName());
        ElasticIndexOperations.closeIndex(CONFERENCE_EDITION.indexName());
    }

    private void initIndex(String indexName) throws InterruptedException {
        ElasticIndexOperations.createIndex(indexName);
        ElasticIndexOperations.createMapping(indexName);
    }

    public void indexConference(Conference conference) throws InterruptedException {
        ElasticIndexOperations.writeConference(
                Objects.requireNonNull(getMapper()).convertToConferenceDTO(conference.getTitle()), CONFERENCE.indexName()
        );
    }

    public void indexConferenceEdition(ConferenceEdition edition, Conference conference) throws InterruptedException {
        conference.addConferenceEdition(edition);
        repository.updateConference(conference);
        ElasticIndexOperations.writeConferenceEdition(
                Objects.requireNonNull(getMapper()).convertToConferenceEditionDTO(edition.getId(), CONFERENCE_EDITION.indexName())
        );
        indexConference(conference);
    }
}
