package masterthesis.conferences.server.rest.storage;

import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.GetRequest;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import masterthesis.conferences.ConferencesApplication;
import masterthesis.conferences.data.dto.AdditionalMetricDTO;
import masterthesis.conferences.data.dto.ConferenceDTO;
import masterthesis.conferences.data.dto.ConferenceEditionDTO;
import masterthesis.conferences.data.dto.IngestConfigurationDTO;
import masterthesis.conferences.data.model.AdditionalMetric;
import masterthesis.conferences.data.model.Conference;
import masterthesis.conferences.data.model.ConferenceEdition;
import masterthesis.conferences.data.model.IngestConfiguration;
import masterthesis.conferences.server.controller.StorageController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static masterthesis.conferences.data.util.Indices.*;

public class ElasticReadOperation extends ElasticOperation {
    public static boolean existsIndex(String indexName) throws ExecutionException, InterruptedException {
        return esClient.indices().exists(ExistsRequest.of(e -> e.index(indexName)))
                .whenComplete((response, exception) -> {
                    if (exception != null) {
                        ConferencesApplication.getLogger().error("Failed to fetch index", exception);
                        ConferencesApplication.getErrorChecker().detectError();
                    }
                }).get().value();
    }

    public static Conference retrieveConference(String title) throws InterruptedException, ExecutionException {
        return Objects.requireNonNull(StorageController.getMapper()).convertToConference((ConferenceDTO) sendAsyncRequestToElastic(
                GetRequest.of(s -> s.index(CONFERENCE.index()).id(title)),
                ConferenceDTO.class
        ));
    }

    public static List<Conference> retrieveConferences() throws InterruptedException, ExecutionException {
        List<Hit<ConferenceDTO>> hits = (List<Hit<ConferenceDTO>>) sendAsyncSearchRequestToElastic(
                SearchRequest.of(s -> s.index(CONFERENCE.index()).query(Query.of(m -> m.matchAll(MatchAllQuery.of(q -> q)))))
        );
        List<ConferenceDTO> conferenceDTOList = new ArrayList<>();
        for (Hit<ConferenceDTO> hit : hits) {
            conferenceDTOList.add(hit.source());
        }
        return Objects.requireNonNull(StorageController.getMapper()).convertToConferenceList(conferenceDTOList);
    }

    public static ConferenceEdition retrieveConferenceEdition(int id) throws InterruptedException, ExecutionException {
        return ConferenceEditionDTO.convertToConferenceEdition((ConferenceEditionDTO) sendAsyncRequestToElastic(
                        GetRequest.of(s -> s.index(CONFERENCE_EDITION.index()).id(Integer.toString(id))),
                        ConferenceEditionDTO.class
                ));
    }

    public static AdditionalMetric retrieveAdditionalMetric(int id) throws ExecutionException, InterruptedException {
        return AdditionalMetricDTO.convertToAdditionalMetric((AdditionalMetricDTO) sendAsyncRequestToElastic(
                        GetRequest.of(s -> s.index(ADDITIONAL_METRIC.index()).id(Integer.toString(id))),
                        AdditionalMetricDTO.class
                ));
    }

    public static IngestConfiguration retrieveIngestConfiguration(int id) throws ExecutionException, InterruptedException {
        return IngestConfigurationDTO.convertToIngestConfiguration((IngestConfigurationDTO) sendAsyncRequestToElastic(
                        GetRequest.of(s -> s.index(INGEST_CONFIGURATION.index()).id(Integer.toString(id))),
                        IngestConfigurationDTO.class
                ));
    }

    protected static Object sendAsyncRequestToElastic(GetRequest request, Class<?> clazz) throws InterruptedException, ExecutionException {
        CompletableFuture<?> responseObject = null;
        if (request != null) {
            responseObject = esClient.get(request, clazz)
                    .whenComplete((response, exception) -> {
                        if (exception != null) {
                            ConferencesApplication.getLogger().error("Failed to retrieve item", exception);
                            ConferencesApplication.getErrorChecker().detectError();
                        } else {
                            ConferencesApplication.getLogger().info("Item retrieved");
                        }
                    });
        }
        assert responseObject != null;
        while (!responseObject.isDone()) {
            Thread.sleep(100);
        }
        return ((GetResponse<?>) responseObject.get()).source();
    }

    protected static Object sendAsyncSearchRequestToElastic(SearchRequest request) throws InterruptedException, ExecutionException {
        CompletableFuture<?> responseObject = null;
        if (request != null) {
            responseObject = esClient.search(request, (Class<?>) masterthesis.conferences.data.dto.ConferenceDTO.class)
                    .whenComplete((response, exception) -> {
                        if (exception != null) {
                            ConferencesApplication.getLogger().error("Failed to retrieve item", exception);
                            ConferencesApplication.getErrorChecker().detectError();
                        } else {
                            ConferencesApplication.getLogger().info("Item retrieved");
                        }
                    });
        }
        assert responseObject != null;
        while (!responseObject.isDone()) {
            Thread.sleep(100);
        }
        return ((SearchResponse<?>) responseObject.get()).hits().hits();
    }

}
