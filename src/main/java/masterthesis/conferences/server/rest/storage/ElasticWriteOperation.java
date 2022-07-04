package masterthesis.conferences.server.rest.storage;

import co.elastic.clients.elasticsearch.core.IndexRequest;
import masterthesis.conferences.ConferencesApplication;

import java.util.concurrent.CompletableFuture;

public abstract class ElasticWriteOperation extends ElasticOperation{

    protected static void sendAsyncRequestToElastic(Object request) throws InterruptedException {
        CompletableFuture<?> responseObject = null;
        if (request instanceof IndexRequest) {
            IndexRequest<?> indexRequest = (IndexRequest<?>) request;
            responseObject = esClient.index(indexRequest)
                    .whenComplete((response, exception) -> {
                        if (exception != null) {
                            ConferencesApplication.getLogger().error("Failed to index", exception);
                            ConferencesApplication.getErrorChecker().detectError();
                        } else {
                            ConferencesApplication.getLogger().info("Item indexed");
                        }
                    });
        }
        while (responseObject != null && !responseObject.isDone()) {
            Thread.sleep(600);
        }

    }
}
