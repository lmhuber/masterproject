package masterthesis.conferences.server.controller.storage;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.core.GetRequest;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import masterthesis.conferences.ConferencesApplication;
import masterthesis.conferences.data.metrics.ApplicationType;
import masterthesis.conferences.data.model.AdditionalMetric;
import masterthesis.conferences.data.model.Conference;
import masterthesis.conferences.data.model.ConferenceEdition;
import masterthesis.conferences.data.model.IngestConfiguration;
import masterthesis.conferences.data.model.dto.AdditionalMetricDTO;
import masterthesis.conferences.data.model.dto.ConferenceEditionDTO;
import masterthesis.conferences.data.model.dto.IngestConfigurationDTO;
import masterthesis.conferences.server.controller.Controller;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static masterthesis.conferences.data.util.Indices.*;
import static masterthesis.conferences.server.controller.storage.ElasticReadOperation.sendAsyncRequestToElastic;

public class StorageController implements Controller {
    private static StorageController instance = null;
    private static ElasticsearchAsyncClient client = null;
    private static MapperService mapperService = null;

    private static final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    private static final Set<Conference> conferenceSet = new HashSet<>();
    private static final String SAMPLES_PATH = "src/main/resources/samples/";

    public static StorageController getInstance() {
        if (instance == null) {
            instance = new StorageController();
            instance.fetchConferences();
        }
        return instance;
    }

    public static ElasticsearchAsyncClient getElasticInstance() {
        if (client == null) {
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials("elastic", "changeme"));
            RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200))
                    .setHttpClientConfigCallback(httpAsyncClientBuilder ->
                            httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider)).build();
            client = new ElasticsearchAsyncClient(new RestClientTransport(restClient, new JacksonJsonpMapper()));
        }
        return client;
    }

    public static MapperService getMapper() {
        if (mapperService == null) {
            mapperService = new MapperService();
        }
        return mapperService;
    }


    @Override
    public synchronized void init() throws ExecutionException, InterruptedException {
        ConferencesApplication.getLogger().info("Initializing Controller");
        getInstance();
        getMapper();
        ConferencesApplication.getLogger().info("Controller Objects initialized");
        ConferencesApplication.getLogger().info("Initializing Elasticsearch Index");
        boolean indexCreated;
        if (ConferencesApplication.DEBUG) {
            if (ElasticReadOperation.existsIndex(CONFERENCE.index())) {
                ElasticWriteOperation.deleteIndex(CONFERENCE.index());
            }
            if (ElasticReadOperation.existsIndex(CONFERENCE_EDITION.index())) {
                ElasticWriteOperation.deleteIndex(CONFERENCE_EDITION.index());
            }
            if (ElasticReadOperation.existsIndex(ADDITIONAL_METRIC.index())) {
                ElasticWriteOperation.deleteIndex(ADDITIONAL_METRIC.index());
            }
            if (ElasticReadOperation.existsIndex(INGEST_CONFIGURATION.index())) {
                ElasticWriteOperation.deleteIndex(INGEST_CONFIGURATION.index());
            }

        }

        indexCreated = ElasticReadOperation.existsIndex(CONFERENCE.index());
        indexCreated &= ElasticReadOperation.existsIndex(CONFERENCE_EDITION.index());
        indexCreated &= ElasticReadOperation.existsIndex(ADDITIONAL_METRIC.index());
        indexCreated &= ElasticReadOperation.existsIndex(INGEST_CONFIGURATION.index());
        if (!indexCreated) {
            initIndex(CONFERENCE.index());
            initIndex(CONFERENCE_EDITION.index());
            initIndex(ADDITIONAL_METRIC.index());
            initIndex(INGEST_CONFIGURATION.index());
        } else {
            ElasticWriteOperation.changeIndex(CONFERENCE.index(), true);
            ElasticWriteOperation.changeIndex(CONFERENCE_EDITION.index(), true);
            ElasticWriteOperation.changeIndex(ADDITIONAL_METRIC.index(), true);
            ElasticWriteOperation.changeIndex(INGEST_CONFIGURATION.index(), true);
        }
        ConferencesApplication.getLogger().info("Elasticsearch Index initialized");
        if (ConferencesApplication.LOAD_SAMPLES && ConferencesApplication.DEBUG) {
            ConferencesApplication.getLogger().info("Initializing Sample Data");
            ConferencesApplication.getLogger().warn("This process can take up a significant amount of time," +
                    " as all items have to be indexed, often multiple times for an update.");
            loadSampleData();
            ConferencesApplication.getLogger().info("Initializing Sample Data ... DONE");
        }
        
    }

    private void loadSampleData() {
        try (BufferedReader conferenceReader = new BufferedReader(new FileReader(SAMPLES_PATH + "Conference.csv"));
             BufferedReader editionReader = new BufferedReader(new FileReader(SAMPLES_PATH + "ConferenceEdition.csv"));
             BufferedReader metricReader = new BufferedReader(new FileReader(SAMPLES_PATH + "AdditionalMetric.csv"));
             BufferedReader ingestReader = new BufferedReader(new FileReader(SAMPLES_PATH + "IngestConfiguration.csv"))) {
            String conferenceLine;
            String editionLine;
            String metricLine;
            String ingestLine;
            int confId = 1;
            int editionId = 1;
            int metricId = 1;
            // Discard title line
            conferenceReader.readLine();
            editionReader.readLine();
            metricReader.readLine();
            ingestReader.readLine();
            while ((conferenceLine = conferenceReader.readLine()) != null) {
                String[] confFields = conferenceLine.split(";");
                Conference conference = new Conference(confFields[0], confFields[1], confFields[2]);
                indexConference(conference);
                while ((editionLine = editionReader.readLine()) != null
                        && editionLine.startsWith(Integer.toString(confId))) {
                    editionReader.mark(1000);
                    String[] editionFields = editionLine.replace(",",".").split(";");
                    ConferenceEdition edition = new ConferenceEdition(editionId,
                            Integer.parseInt(editionFields[1]),Integer.parseInt(editionFields[2]),
                            Integer.parseInt(editionFields[3]), Integer.parseInt(editionFields[4]),
                            Integer.parseInt(editionFields[9]), Float.parseFloat(editionFields[5]),
                            Float.parseFloat(editionFields[6]), Float.parseFloat(editionFields[7]),
                            editionFields[8], editionFields[11], editionFields[10]);
                    conference.addConferenceEdition(edition);
                    indexConferenceEdition(edition, conference);
                    while ((metricLine = metricReader.readLine()) != null
                            && metricLine.split(";")[2].equals(Integer.toString(editionId))){
                        metricReader.mark(1000);
                        String[] metricFields = metricLine.replace(",", ".").split(";");
                        IngestConfiguration ingest = null;
                        if ((ingestLine = ingestReader.readLine()) != null) {
                            String[] ingestFields = ingestLine.replace(",", ".").split(";");
                            ingest = new IngestConfiguration(Integer.parseInt(ingestFields[0]),
                                    ApplicationType.getFromString(ingestFields[1]));
                        }
                        AdditionalMetric metric = new AdditionalMetric(metricId, ingest ,Float.parseFloat(metricFields[1]), metricFields[0]);
                        edition.addAdditionalMetric(metric);
                        indexAdditionalMetric(edition, conference, metric);
                        indexIngestConfiguration(edition, conference, metric, ingest);
                        metricId++;
                    }
                    editionId++;
                    metricReader.reset();
                }
                confId++;
                editionReader.reset();

            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shutdown() throws InterruptedException {
        ElasticWriteOperation.changeIndex(CONFERENCE.index(), false);
        ElasticWriteOperation.changeIndex(CONFERENCE_EDITION.index(), false);
        ElasticWriteOperation.changeIndex(ADDITIONAL_METRIC.index(), false);
        ElasticWriteOperation.changeIndex(INGEST_CONFIGURATION.index(), false);
    }

    private void initIndex(String indexName) throws InterruptedException {
        ElasticWriteOperation.createIndex(indexName);
        ElasticWriteOperation.createMapping(indexName);
    }

    public Conference getConference(String title) {
        return conferenceSet.stream().filter(c -> c.getTitle().equals(title)).findFirst().orElse(null);
    }

    public List<Conference> getConferences() {
        return new ArrayList<>(conferenceSet);
    }

    public void save(Conference conference) throws InterruptedException {
        conferenceSet.remove(conference);
        conferenceSet.add(conference);
        indexConference(conference);
    }

    public void deleteById(String title) throws InterruptedException {
        removeConference(getConference(title));
        conferenceSet.remove(getConference(title));
    }

    public Optional<Conference> findById(String title) {
        Conference conference = getConference(title);
        if (conference == null) return Optional.empty();
        return Optional.of(conference);
    }

    public ConferenceEdition getEdition(int id) {
        for (Conference c : conferenceSet) {
            if (c.getConferenceEditionIds()!= null && c.getConferenceEditionIds().contains(id)) {
                for (ConferenceEdition edition : c.getConferenceEditions()) {
                    if (edition.getId() == id) return edition;
                }
            }
        }
        return null;
    }

    public AdditionalMetric getMetric(int id) {
        for (Conference c : conferenceSet) {
            for (ConferenceEdition e : c.getConferenceEditions()) {
                for (AdditionalMetric metric : e.getAdditionalMetrics()) {
                    if (metric.getId() == id) return metric;
                }
            }
        }
        return null;
    }

    public ConferenceEdition getEditonByMetric(int id) {
        for (Conference c : conferenceSet) {
            for (ConferenceEdition edition : c.getConferenceEditions()) {
                if (edition.getAdditionalMetricIds().contains(id)) {
                    return edition;
                }
            }
        }
        return null;
    }

    public IngestConfiguration getIngestConfiguration(int ingestConfigId) {
        for (Conference c : conferenceSet) {
            for (ConferenceEdition edition : c.getConferenceEditions()) {
                for (AdditionalMetric metric : edition.getAdditionalMetrics()) {
                    if (metric.getConfig() != null && metric.getConfig().getId() == ingestConfigId) {
                        return metric.getConfig();
                    }
                }
            }
        }
        return null;
    }

    public void indexConference(Conference conference) throws InterruptedException {
        conferenceSet.add(conference);
        ElasticWriteOperation.writeConference(
                Objects.requireNonNull(getMapper()).convertToConferenceDTO(conference.getTitle()), CONFERENCE.index()
        );
    }

    public void indexConferenceEdition(ConferenceEdition edition, Conference conference) throws InterruptedException {
        conference.addConferenceEdition(edition);
        conferenceSet.remove(conference);
        conferenceSet.add(conference);
        ElasticWriteOperation.writeConferenceEdition(
                Objects.requireNonNull(getMapper()).convertToConferenceEditionDTO(edition.getId())
        );
        indexConference(conference);
    }

    public void indexAdditionalMetric(ConferenceEdition edition, Conference conference, AdditionalMetric metric) throws InterruptedException {
        edition.addAdditionalMetric(metric);
        edition.updateAdditionalMetric(metric);
        ElasticWriteOperation.writeAdditionalMetric(
                Objects.requireNonNull(getMapper()).convertToAdditionalMetricDTO(metric.getId())
        );
        indexConferenceEdition(edition, conference);
    }

    public void indexIngestConfiguration(ConferenceEdition edition, Conference conference, AdditionalMetric metric, IngestConfiguration config) throws InterruptedException {
        metric.setConfig(config);
        IngestConfigurationDTO ingestDTO = Objects.requireNonNull(getMapper()).convertToIngestConfigurationDTO(config.getId());
        if (ingestDTO == null) return;
        ElasticWriteOperation.writeIngestConfiguration(ingestDTO);
        indexAdditionalMetric(edition, conference, metric);
    }

    public void removeConference(Conference conference) throws InterruptedException {
        if (conference.getConferenceEditions() != null) {
            for (ConferenceEdition edition : conference.getConferenceEditions()) {
                removeConferenceEdition(edition);
            }
        }
        ElasticWriteOperation.deleteConference(conference.getTitle());
        conferenceSet.remove(conference);
    }

    public void removeConferenceEdition(ConferenceEdition edition) throws InterruptedException {
        if (edition.getAdditionalMetrics() != null) {
            for (AdditionalMetric metric : edition.getAdditionalMetrics()) {
                removeAdditionalMetric(metric);
            }
        }
        ElasticWriteOperation.deleteConferenceEdition(edition.getId());
        final int id = edition.getId();
        Conference conferenceToRemove = null;
        ConferenceEdition editionToRemove = null;
        for (Conference c : conferenceSet) {
            if (c.getConferenceEditionIds().contains(id)) {
                for (ConferenceEdition conferenceEdition : c.getConferenceEditions()) {
                    if (edition.getId() == id) {
                        editionToRemove = conferenceEdition;
                        conferenceToRemove = c;
                    }
                }
            }
        }
        if (editionToRemove != null) {
            conferenceToRemove.getConferenceEditions().remove(editionToRemove);
        }
    }

    public void removeAdditionalMetric(AdditionalMetric metric) throws InterruptedException {
        removeIngestConfiguration(metric.getConfig());
        ElasticWriteOperation.deleteAdditionalMetric(metric.getId());
        final int id = metric.getId();
        ConferenceEdition editionToRemove = null;
        AdditionalMetric metricToRemove = null;
        for (Conference c : conferenceSet) {
            for (ConferenceEdition edition : c.getConferenceEditions()) {
                if (edition.getAdditionalMetricIds().contains(id)) {
                    for (AdditionalMetric additionalMetric : edition.getAdditionalMetrics()) {
                        if (metric.getId() == id) {
                            editionToRemove = edition;
                            metricToRemove = additionalMetric;
                        }
                    }
                }
            }
        }
        if (editionToRemove != null) {
            editionToRemove.getAdditionalMetrics().remove(metricToRemove);
        }
    }

    public void removeIngestConfiguration(IngestConfiguration config) throws InterruptedException {
        if (config == null) return;
        ElasticWriteOperation.deleteIngestConfiguration(config.getId());
        final int id = config.getId();
        AdditionalMetric metricToRemove = null;
        for (Conference c : conferenceSet) {
            for (ConferenceEdition edition : c.getConferenceEditions()) {
                if (edition.getAdditionalMetricIds().contains(id)) {
                    for (AdditionalMetric metric : edition.getAdditionalMetrics()) {
                        if (metric.getConfig().getId() == id) {
                            metricToRemove = metric;
                        }
                    }
                }
            }
        }
        if (metricToRemove != null) {
            metricToRemove.setConfig(null);
        }
    }


    public ConferenceEdition retrieveConferenceEdition(int id) throws InterruptedException, ExecutionException {
        return ConferenceEditionDTO.convertToConferenceEdition((ConferenceEditionDTO) sendAsyncRequestToElastic(
                GetRequest.of(s -> s.index(CONFERENCE_EDITION.index()).id(Integer.toString(id))),
                ConferenceEditionDTO.class
        ));
    }

    public AdditionalMetric retrieveAdditionalMetric(int id) throws ExecutionException, InterruptedException {
        return AdditionalMetricDTO.convertToAdditionalMetric((AdditionalMetricDTO) sendAsyncRequestToElastic(
                GetRequest.of(s -> s.index(ADDITIONAL_METRIC.index()).id(Integer.toString(id))),
                AdditionalMetricDTO.class
        ));
    }

    public IngestConfiguration retrieveIngestConfiguration(int id) throws ExecutionException, InterruptedException {
        return IngestConfigurationDTO.convertToIngestConfiguration((IngestConfigurationDTO) sendAsyncRequestToElastic(
                GetRequest.of(s -> s.index(INGEST_CONFIGURATION.index()).id(Integer.toString(id))),
                IngestConfigurationDTO.class
        ));
    }

    public void fetchConferences() {
        List<Conference> conferenceList = new ArrayList<>();
        try {
            conferenceList = ElasticReadOperation.retrieveConferences();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        conferenceSet.addAll(conferenceList);
    }


}
