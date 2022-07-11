package masterthesis.conferences.server.rest.storage;

import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.GetRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import masterthesis.conferences.ConferencesApplication;
import masterthesis.conferences.data.dto.AdditionalMetricDTO;
import masterthesis.conferences.data.dto.ConferenceDTO;
import masterthesis.conferences.data.dto.ConferenceEditionDTO;
import masterthesis.conferences.data.model.AdditionalMetric;
import masterthesis.conferences.data.model.Conference;
import masterthesis.conferences.data.model.ConferenceEdition;
import masterthesis.conferences.server.controller.ServerController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static masterthesis.conferences.data.util.Indices.*;

public class ElasticSearchOperations extends ElasticReadOperation {
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
        return Objects.requireNonNull(ServerController.getMapper()).convertToConference((ConferenceDTO) sendAsyncRequestToElastic(
                GetRequest.of(s -> s.index(CONFERENCE.indexName()).id(title)),
                ConferenceDTO.class
        ));
    }

    public static List<Conference> retrieveConferences() throws InterruptedException, ExecutionException {
        List<Hit<ConferenceDTO>> hits = (List<Hit<ConferenceDTO>>) sendAsyncSearchRequestToElastic(
                SearchRequest.of(s -> s.index(CONFERENCE.indexName()).query(Query.of(m -> m.matchAll(MatchAllQuery.of(q -> q)))))
        );
        List<ConferenceDTO> conferenceDTOList = new ArrayList<>();
        for (Hit<ConferenceDTO> hit : hits) {
            conferenceDTOList.add(hit.source());
        }
        return Objects.requireNonNull(ServerController.getMapper()).convertToConferenceList(conferenceDTOList);
    }

    public static ConferenceEdition retrieveConferenceEdition(int id) throws InterruptedException, ExecutionException {
        return Objects.requireNonNull(ServerController.getMapper())
                .convertToConferenceEdition((ConferenceEditionDTO) sendAsyncRequestToElastic(
                        GetRequest.of(s -> s.index(CONFERENCE_EDITION.indexName()).id(Integer.toString(id))),
                        ConferenceEditionDTO.class
                ));
    }

    public static AdditionalMetric retrieveAdditionalMetric(int id) throws ExecutionException, InterruptedException {
        return Objects.requireNonNull(ServerController.getMapper())
                .convertToAdditionalMetric((AdditionalMetricDTO) sendAsyncRequestToElastic(
                        GetRequest.of(s -> s.index(ADDITIONAL_METRIC.indexName()).id(Integer.toString(id))),
                        AdditionalMetricDTO.class
                ));
    }
}
