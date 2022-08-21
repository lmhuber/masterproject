package masterthesis.conferences.server.controller.storage;

import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.GetRequest;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import masterthesis.conferences.ConferencesApplication;
import masterthesis.conferences.data.model.Conference;
import masterthesis.conferences.data.model.dto.ConferenceDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static masterthesis.conferences.data.util.Indices.CONFERENCE;

@SuppressWarnings("unchecked")
public class ElasticReadOperation extends ElasticOperation {
    /**
     * Checks if an elastic index is created
     *
     * @param indexName the index name
     * @return true if created, false otherwise
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static boolean existsIndex(String indexName) throws ExecutionException, InterruptedException {
        return esClient.indices().exists(ExistsRequest.of(e -> e.index(indexName)))
                .whenComplete((response, exception) -> {
                    if (exception != null) {
                        ConferencesApplication.getLogger().error("Failed to fetch index", exception);
                        ConferencesApplication.getErrorChecker().detectError();
                    }
                }).get().value();
    }

    /**
     * Retrieves all conferences to work with them in the services
     *
     * @return list of conferences
     * @throws InterruptedException
     * @throws ExecutionException
     */
    protected static List<Conference> retrieveConferences() throws InterruptedException, ExecutionException {
        List<Hit<ConferenceDTO>> hits = (List<Hit<ConferenceDTO>>) sendAsyncSearchRequestToElastic(
                SearchRequest.of(s -> s.index(CONFERENCE.index()).query(Query.of(m -> m.matchAll(MatchAllQuery.of(q -> q)))))
        );
        List<ConferenceDTO> conferenceDTOList = new ArrayList<>();
        for (Hit<ConferenceDTO> hit : hits) {
            conferenceDTOList.add(hit.source());
        }
        return Objects.requireNonNull(StorageController.getMapper()).convertToConferenceList(conferenceDTOList);
    }


    /**
     * Sends an asynchronous request to elasticsearch to retrieve an item from any of the indices
     *
     * @param request a get request to elastic to obtain one item
     * @param clazz the DTO class of the returned object
     * @return a DTO object containing an item of any of the indices
     * @throws InterruptedException
     * @throws ExecutionException
     */
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


    /**
     * Sends an asynchronous request to elasticsearch to retrieve items from any of the indices
     *
     * @param request a search request to elastic to obtain an arbitrary number of items
     * @return a DTO object containing an item of any of the indices
     * @throws InterruptedException
     * @throws ExecutionException
     */
    protected static Object sendAsyncSearchRequestToElastic(SearchRequest request) throws InterruptedException, ExecutionException {
        CompletableFuture<?> responseObject = null;
        if (request != null) {
            responseObject = esClient.search(request, (Class<?>) ConferenceDTO.class)
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
