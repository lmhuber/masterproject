package masterthesis.conferences.server.rest.storage;

import masterthesis.conferences.ConferencesApplication;

import java.util.concurrent.ExecutionException;

import static masterthesis.conferences.data.util.Indices.CONFERENCE_EDITION;

public class ElasticCountOperations extends ElasticReadOperation {
    private static int getDocumentNumberFromIndex(String index) throws ExecutionException, InterruptedException {
        return (int) StorageController.getInstance().count(c -> c.index(index)).whenComplete((response, exception) -> {
            if (exception != null) {
                ConferencesApplication.getLogger().error("Could not retrieve document count", exception);
                ConferencesApplication.getErrorChecker().detectError();
            }
        }).get().count();
    }

    public static int getMaxConferenceEditionId() throws ExecutionException, InterruptedException {
        return getDocumentNumberFromIndex(CONFERENCE_EDITION.indexName());
    }
}
