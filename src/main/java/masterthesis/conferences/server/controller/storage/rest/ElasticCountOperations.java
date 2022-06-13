package masterthesis.conferences.server.controller.storage.rest;

import masterthesis.conferences.ConferencesApplication;
import masterthesis.conferences.server.controller.storage.StorageController;

import java.util.concurrent.ExecutionException;

public class ElasticCountOperations extends ElasticReadOperation {
    public static int getDocumentNumberFromIndex(String index) throws ExecutionException, InterruptedException {
        return (int) StorageController.getInstance().count(c -> c.index(index)).whenComplete((response, exception) -> {
            if (exception != null)
                ConferencesApplication.getLogger().error("Could not retrieve document count", exception);
        }).get().count();
    }
}
