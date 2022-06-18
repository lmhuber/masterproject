package masterthesis.conferences.server.controller.storage.rest;

import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import masterthesis.conferences.ConferencesApplication;

import java.util.concurrent.ExecutionException;

public class ElasticSearchOperations extends ElasticReadOperation {
    public static boolean existsIndex(String indexName) throws ExecutionException, InterruptedException {
        return StorageController.getInstance().indices().exists(ExistsRequest.of(e -> e.index(indexName)))
                .whenComplete((response, exception) -> {
                    if (exception != null) {
                        ConferencesApplication.getLogger().error("Failed to fetch index", exception);
                        ConferencesApplication.getErrorChecker().detectError();
                    }
                }).get().value();
    }
}
