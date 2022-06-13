package masterthesis.conferences.server.controller.storage.rest;

import masterthesis.conferences.ConferencesApplication;
import masterthesis.conferences.server.controller.storage.StorageController;

public class ElasticIndexOperations extends ElasticWriteOperation {
    public static void createIndex(String indexName) {
        StorageController.getInstance().indices().create(i -> i.index(indexName))
                .whenComplete((response, exception)
                        -> {
                    if (exception != null) {
                        ConferencesApplication.getLogger().error("Failed to fetch index", exception);
                    } else {
                        ConferencesApplication.getLogger().info(response);
                    }
                });
    }
}
