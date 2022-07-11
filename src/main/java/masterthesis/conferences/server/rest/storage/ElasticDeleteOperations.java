package masterthesis.conferences.server.rest.storage;

import masterthesis.conferences.ConferencesApplication;

import java.util.concurrent.CompletableFuture;

import static masterthesis.conferences.data.util.Indices.*;

public class ElasticDeleteOperations extends ElasticWriteOperation {
    public static void deleteIndex(String indexName) throws InterruptedException {
        CompletableFuture<?> responseObject = esClient.indices().delete(i -> i.index(indexName))
                .whenComplete((response, exception)
                        -> {
                    if (exception != null) {
                        ConferencesApplication.getLogger().error("Failed to delete index", exception);
                    } else {
                        ConferencesApplication.getLogger().info(response);
                    }
                });
        while (!responseObject.isDone()) {
            Thread.sleep(100);
        }
    }

    public static void deleteConference(String title) throws InterruptedException {
        CompletableFuture<?> responseObject = esClient.delete(
                i -> i.index(CONFERENCE.indexName()).id(title)
        ).whenComplete((response, exception)
                -> {
            if (exception != null) {
                ConferencesApplication.getLogger().error("Failed to delete Conference", exception);
            } else {
                ConferencesApplication.getLogger().info(response);
            }
        });
        while (!responseObject.isDone()) {
            Thread.sleep(100);
        }
    }

    public static void deleteConferenceEdition(int id) throws InterruptedException {
        CompletableFuture<?> responseObject = esClient.delete(
                i -> i.index(CONFERENCE_EDITION.indexName()).id(Integer.toString(id))
        ).whenComplete((response, exception)
                -> {
            if (exception != null) {
                ConferencesApplication.getLogger().error("Failed to delete ConferenceEdition", exception);
            } else {
                ConferencesApplication.getLogger().info(response);
            }
        });
        while (!responseObject.isDone()) {
            Thread.sleep(100);
        }
    }

    public static void deleteAdditionalMetric(int id) throws InterruptedException {
        CompletableFuture<?> responseObject = esClient.delete(
                i -> i.index(ADDITIONAL_METRIC.indexName()).id(Integer.toString(id))
        ).whenComplete((response, exception)
                -> {
            if (exception != null) {
                ConferencesApplication.getLogger().error("Failed to delete AdditionalMetric", exception);
            } else {
                ConferencesApplication.getLogger().info(response);
            }
        });
        while (!responseObject.isDone()) {
            Thread.sleep(100);
        }
    }

}
