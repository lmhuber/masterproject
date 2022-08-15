package masterthesis.conferences.server.controller.storage;

import co.elastic.clients.elasticsearch._types.mapping.DynamicMapping;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import masterthesis.conferences.ConferencesApplication;
import masterthesis.conferences.data.model.dto.AdditionalMetricDTO;
import masterthesis.conferences.data.model.dto.ConferenceDTO;
import masterthesis.conferences.data.model.dto.ConferenceEditionDTO;
import masterthesis.conferences.data.model.dto.IngestConfigurationDTO;
import masterthesis.conferences.data.util.Indices;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static masterthesis.conferences.data.util.Indices.*;

public class ElasticWriteOperation extends ElasticOperation{
    protected static void createIndex(String indexName) throws InterruptedException {
        CompletableFuture<?> responseObject = esClient.indices().create(i -> i.index(indexName))
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

    protected static void createMapping(String indexName) throws InterruptedException {
        Map<String, Property> properties = null;
        if (indexName.equalsIgnoreCase(CONFERENCE.index())) {
            properties = ConferenceDTO.getProperties();
        } else if (indexName.equalsIgnoreCase(CONFERENCE_EDITION.index())) {
            properties = ConferenceEditionDTO.getProperties();
        } else if (indexName.equalsIgnoreCase(ADDITIONAL_METRIC.index())) {
            properties = AdditionalMetricDTO.getProperties();
        }

        if (properties == null) {
            ConferencesApplication.getLogger().warn("Index not implemented for preparation of mapping");
            return;
        }
        final Map<String, Property> propertyMap = properties;
        CompletableFuture<?> responseObject = esClient
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

    protected static void changeIndex(String indexName, boolean open) throws InterruptedException {
        CompletableFuture<?> responseObject;
        if (open) {
            responseObject = esClient.indices().open(i -> i.index(indexName));
        } else {
            responseObject = esClient.indices().close(i -> i.index(indexName));
        }
        responseObject.whenComplete((response, exception)
                -> {
            if (exception != null) {
                ConferencesApplication.getLogger()
                        .error(open ? "Failed to open index" : "Failed to close index", exception);
            } else {
                ConferencesApplication.getLogger().info(response);
            }
        });
        while (!responseObject.isDone()) {
            Thread.sleep(100);
        }
    }

    protected static void writeConference(ConferenceDTO conferenceDTO, String indexName) throws InterruptedException {
        sendAsyncRequestToElastic(
                IndexRequest.of(c -> c.index(indexName).id(conferenceDTO.getTitle()).document(conferenceDTO))
        );
    }

    protected static void writeConferenceEdition(ConferenceEditionDTO editionDTO) throws InterruptedException {
        sendAsyncRequestToElastic(
                IndexRequest.of(c -> c.index(CONFERENCE_EDITION.index())
                        .id(Integer.toString(editionDTO.getId())).document(editionDTO))
        );
    }

    protected static void writeAdditionalMetric(AdditionalMetricDTO additionalMetricDTO) throws InterruptedException {
        sendAsyncRequestToElastic(
                IndexRequest.of(c -> c.index(ADDITIONAL_METRIC.index())
                        .id(Integer.toString(additionalMetricDTO.getMetId())).document(additionalMetricDTO))
        );
    }

    protected static void writeIngestConfiguration(IngestConfigurationDTO ingestConfigurationDTO) throws InterruptedException {
        sendAsyncRequestToElastic(
                IndexRequest.of(c -> c.index(INGEST_CONFIGURATION.index())
                        .id(Integer.toString(ingestConfigurationDTO.getId())).document(ingestConfigurationDTO))
        );
    }


    protected static void deleteIndex(String indexName) throws InterruptedException {
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

    protected static void deleteConference(String title) throws InterruptedException {
        CompletableFuture<?> responseObject = esClient.delete(
                i -> i.index(CONFERENCE.index()).id(title)
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

    protected static void deleteConferenceEdition(int id) throws InterruptedException {
        deleteDocument(id, CONFERENCE_EDITION, "ConferenceEdition");
    }

    protected static void deleteAdditionalMetric(int id) throws InterruptedException {
        deleteDocument(id, ADDITIONAL_METRIC, "AdditionalMetric");
    }

    protected static void deleteIngestConfiguration(int id) throws InterruptedException {
        deleteDocument(id, INGEST_CONFIGURATION, "IngestConfiguration");
    }

    private static void deleteDocument(int id, Indices index, String name) throws InterruptedException {
        CompletableFuture<?> responseObject = esClient.delete(
                i -> i.index(index.index()).id(Integer.toString(id))
        ).whenComplete((response, exception)
                -> {
            if (exception != null) {
                ConferencesApplication.getLogger().error("Failed to delete " + name, exception);
            } else {
                ConferencesApplication.getLogger().info(response);
            }
        });
        while (!responseObject.isDone()) {
            Thread.sleep(100);
        }
    }

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
