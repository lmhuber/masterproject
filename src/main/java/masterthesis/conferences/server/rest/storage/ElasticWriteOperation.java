package masterthesis.conferences.server.rest.storage;

import co.elastic.clients.elasticsearch._types.mapping.DynamicMapping;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import masterthesis.conferences.ConferencesApplication;
import masterthesis.conferences.data.dto.AdditionalMetricDTO;
import masterthesis.conferences.data.dto.ConferenceDTO;
import masterthesis.conferences.data.dto.ConferenceEditionDTO;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static masterthesis.conferences.data.util.Indices.*;

public class ElasticWriteOperation extends ElasticOperation{

    public static void createIndex(String indexName) throws InterruptedException {
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

    public static void createMapping(String indexName) throws InterruptedException {
        Map<String, Property> properties = null;
        if (indexName.equalsIgnoreCase(CONFERENCE.indexName())) {
            properties = ConferenceDTO.getProperties();
        } else if (indexName.equalsIgnoreCase(CONFERENCE_EDITION.indexName())) {
            properties = ConferenceEditionDTO.getProperties();
        } else if (indexName.equalsIgnoreCase(ADDITIONAL_METRIC.indexName())) {
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

    public static void openIndex(String indexName) throws InterruptedException {
        CompletableFuture<?> responseObject = esClient.indices().open(i -> i.index(indexName))
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
        CompletableFuture<?> responseObject = esClient.indices().close(i -> i.index(indexName))
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

    public static void writeAdditionalMetric(AdditionalMetricDTO additionalMetricDTO) throws InterruptedException {
        sendAsyncRequestToElastic(
                IndexRequest.of(c -> c.index(ADDITIONAL_METRIC.indexName())
                        .id(Integer.toString(additionalMetricDTO.getMetId())).document(additionalMetricDTO))
        );
    }

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
