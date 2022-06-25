package masterthesis.conferences.server.rest.storage;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.core.GetRequest;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import masterthesis.conferences.ConferencesApplication;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public abstract class ElasticReadOperation {
    protected static Object sendAsyncRequestToElastic(GetRequest request, Class<?> clazz) throws InterruptedException, ExecutionException {
        ElasticsearchAsyncClient esClient = StorageController.getInstance();
        CompletableFuture<?> responseObject = null;
        if (request != null) {
            responseObject = esClient.get(request, clazz)
                    .whenComplete((response, exception) -> {
                        if (exception != null) {
                            ConferencesApplication.getLogger().error("Failed to retrieve item", exception);
                            ConferencesApplication.getErrorChecker().detectError();
                        } else {
                            ConferencesApplication.getLogger().info("Item retrieved");
                        }
                    });
        }
        assert responseObject != null;
        while (!responseObject.isDone()) {
            Thread.sleep(100);
        }
        return ((GetResponse<?>) responseObject.get()).source();
    }

    protected static Object sendAsyncSearchRequestToElastic(SearchRequest request, Class<?> clazz) throws InterruptedException, ExecutionException {
        ElasticsearchAsyncClient esClient = StorageController.getInstance();
        CompletableFuture<?> responseObject = null;
        if (request != null) {
            responseObject = esClient.search(request, clazz)
                    .whenComplete((response, exception) -> {
                        if (exception != null) {
                            ConferencesApplication.getLogger().error("Failed to retrieve item", exception);
                            ConferencesApplication.getErrorChecker().detectError();
                        } else {
                            ConferencesApplication.getLogger().info("Item retrieved");
                        }
                    });
        }
        assert responseObject != null;
        while (!responseObject.isDone()) {
            Thread.sleep(100);
        }
        return ((SearchResponse<?>) responseObject.get()).hits().hits();
    }
}
