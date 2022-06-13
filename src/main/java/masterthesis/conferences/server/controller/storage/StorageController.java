package masterthesis.conferences.server.controller.storage;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import masterthesis.conferences.ConferencesApplication;
import masterthesis.conferences.server.controller.Controller;
import masterthesis.conferences.server.controller.storage.rest.ElasticIndexOperations;
import masterthesis.conferences.server.controller.storage.rest.ElasticSearchOperations;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;

import java.util.concurrent.ExecutionException;

import static masterthesis.conferences.data.util.Indices.CONFERENCE;
import static masterthesis.conferences.data.util.Indices.CONFERENCE_EDITION;

public class StorageController implements Controller {
    private static ElasticsearchAsyncClient client = null;

    private static final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

    public StorageController() {
    }

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
}
