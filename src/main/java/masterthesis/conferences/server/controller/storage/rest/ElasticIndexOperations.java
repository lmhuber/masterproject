package masterthesis.conferences.server.controller.storage.rest;

import co.elastic.clients.elasticsearch._types.mapping.DynamicMapping;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import masterthesis.conferences.ConferencesApplication;
import masterthesis.conferences.data.dto.ConferenceDTO;
import masterthesis.conferences.data.dto.ConferenceEditionDTO;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static masterthesis.conferences.data.util.Indices.CONFERENCE;
import static masterthesis.conferences.data.util.Indices.CONFERENCE_EDITION;

public class ElasticIndexOperations extends ElasticWriteOperation {
    public static void createIndex(String indexName) throws InterruptedException {
        CompletableFuture<?> responseObject = StorageController.getInstance().indices().create(i -> i.index(indexName))
                .whenComplete((response, exception)
                        -> {
                    if (exception != null) {
                        ConferencesApplication.getLogger().error("Failed to fetch index", exception);
                        ConferencesApplication.getErrorChecker().detectError();
                    } else {
                        ConferencesApplication.getLogger().info(response);
                    }
                });
        while (!responseObject.isDone()) {
            Thread.sleep(100);
        }
    }

    public static void createMapping(String indexName) throws InterruptedException {
        Map<String, Property> properties = null;
        if (indexName.equalsIgnoreCase(CONFERENCE.indexName())) {
            properties = ConferenceDTO.getProperties();
        } else if (indexName.equalsIgnoreCase(CONFERENCE_EDITION.indexName())) {
            properties = ConferenceEditionDTO.getProperties();
        }

        if (properties == null) {
            ConferencesApplication.getLogger().warn("Index not implemented for preparation of mapping");
            return;
        }
        final Map<String, Property> propertyMap = properties;
        CompletableFuture<?> responseObject = StorageController.getInstance()
                .indices().putMapping(m -> m.index(indexName)
                        .properties(propertyMap).dynamic(DynamicMapping.False))
                .whenComplete((response, exception) -> {
                    if (exception != null) {
                        ConferencesApplication.getLogger().error("Failed to create mapping", exception);
                        ConferencesApplication.getErrorChecker().detectError();
                    } else {
                        ConferencesApplication.getLogger().info(response);
                    }
                });
        while (!responseObject.isDone()) {
            Thread.sleep(100);
        }
    }

    public static void deleteIndex(String indexName) throws InterruptedException {
        CompletableFuture<?> responseObject = StorageController.getInstance().indices().delete(i -> i.index(indexName))
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

    public static void openIndex(String indexName) throws InterruptedException {
        CompletableFuture<?> responseObject = StorageController.getInstance().indices().open(i -> i.index(indexName))
                .whenComplete((response, exception)
                        -> {
                    if (exception != null) {
                        ConferencesApplication.getLogger().error("Failed to open index", exception);
                    } else {
                        ConferencesApplication.getLogger().info(response);
                    }
                });
        while (!responseObject.isDone()) {
            Thread.sleep(100);
        }
    }

    public static void closeIndex(String indexName) throws InterruptedException {
        CompletableFuture<?> responseObject = StorageController.getInstance().indices().close(i -> i.index(indexName))
                .whenComplete((response, exception)
                        -> {
                    if (exception != null) {
                        ConferencesApplication.getLogger().error("Failed to close index", exception);
                    } else {
                        ConferencesApplication.getLogger().info(response);
                    }
                });
        while (!responseObject.isDone()) {
            Thread.sleep(100);
        }
    }

    public static void writeConference(ConferenceDTO conferenceDTO, String indexName) throws InterruptedException {
        sendAsyncRequestToElastic(
                IndexRequest.of(c -> c.index(indexName).id(conferenceDTO.getTitle()).document(conferenceDTO))
        );
    }

    public static void writeConferenceEdition(ConferenceEditionDTO editionDTO) throws InterruptedException {
        sendAsyncRequestToElastic(
                IndexRequest.of(c -> c.index(CONFERENCE_EDITION.indexName())
                        .id(Integer.toString(editionDTO.getId())).document(editionDTO))
        );
    }
}
