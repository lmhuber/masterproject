package masterthesis.conferences.server.controller.storage.rest;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import masterthesis.conferences.ConferencesApplication;
import masterthesis.conferences.server.controller.storage.StorageController;

public abstract class ElasticWriteOperation {

    private void sendAsyncRequestToElastic(Object request) {
        ElasticsearchAsyncClient esClient = StorageController.getInstance();
        if (request instanceof IndexRequest) {
            IndexRequest<?> indexRequest = (IndexRequest<?>) request;
            esClient.index(indexRequest)
                    .whenComplete((response, exception) -> {
                        if (exception != null) {
                            ConferencesApplication.getLogger().error("Failed to index", exception);
                        } else {
                            ConferencesApplication.getLogger().info("Item indexed");
                        }
                    });
        }

    }
}
