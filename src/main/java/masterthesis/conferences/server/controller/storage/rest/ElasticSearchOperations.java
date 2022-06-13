package masterthesis.conferences.server.controller.storage.rest;

import masterthesis.conferences.ConferencesApplication;
import masterthesis.conferences.server.controller.storage.StorageController;

import java.util.concurrent.ExecutionException;

public class ElasticSearchOperations extends ElasticReadOperation {
    public static boolean existsIndex(String indexName) throws ExecutionException, InterruptedException {
        return StorageController.getInstance().exists(b -> b.index("conference-edition"))
                .whenComplete((response, exception) -> {
                    if (exception != null) {
                        ConferencesApplication.getLogger().error("Failed to fetch index", exception);
                    }
                }).get().value();
    }
}
