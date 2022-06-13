package masterthesis.conferences.server.controller.storage.rest;

import masterthesis.conferences.ConferencesApplication;
import masterthesis.conferences.server.controller.storage.StorageController;

public class ElasticIndexOperations extends ElasticWriteOperation {
    public static void createIndex(String indexName) {
        StorageController.getInstance().index(i -> i.index(indexName))
                .whenComplete((response, exception)
                        -> ConferencesApplication.getLogger().info(indexName + " index created!"));
    }
}
