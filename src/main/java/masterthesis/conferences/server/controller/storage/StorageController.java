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

public class StorageController implements Controller {
    private static ElasticsearchAsyncClient client = null;

    private static final String CONFERENCE = "conference";
    private static final String CONFERENCE_EDITION = "conference-edition";

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

        indexCreated = ElasticSearchOperations.existsIndex(CONFERENCE);
        indexCreated &= ElasticSearchOperations.existsIndex(CONFERENCE_EDITION);
        if (!indexCreated) {
            ElasticIndexOperations.createIndex(CONFERENCE);
            ElasticIndexOperations.createIndex(CONFERENCE_EDITION);
        }
        ConferencesApplication.getLogger().info("Elasticsearch Index initialized");
    }

    @Override
    public void shutdown() {

    }
}
