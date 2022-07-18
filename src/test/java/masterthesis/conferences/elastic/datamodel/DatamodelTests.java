package masterthesis.conferences.elastic.datamodel;

import masterthesis.conferences.ConferencesApplication;
import masterthesis.conferences.data.dto.ConferenceDTO;
import masterthesis.conferences.data.dto.DashboardingMetricDTO;
import masterthesis.conferences.data.metrics.zoom.AudioLatency;
import masterthesis.conferences.data.model.AdditionalMetric;
import masterthesis.conferences.data.model.Conference;
import masterthesis.conferences.data.model.ConferenceEdition;
import masterthesis.conferences.server.controller.ServerController;
import masterthesis.conferences.server.controller.StorageController;
import masterthesis.conferences.server.dashboarding.ChartType;
import masterthesis.conferences.server.dashboarding.DashboardingUtils;
import masterthesis.conferences.server.dashboarding.Operations;
import masterthesis.conferences.server.rest.storage.ElasticReadOperation;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static masterthesis.conferences.metrics.APIMetricTests.TEST_JSON_RESPONSE;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DatamodelTests {

    private static final ServerController controller = new ServerController();
    private static final Set<ConferenceEdition> conferenceEditions = new HashSet<>();
    private static final AudioLatency audioLatency = new AudioLatency();

    private static final String DEXA = "DEXA_UNIT_TEST_1";
    private static final String IIWAS = "iiWAS & MoMM_UNIT_TEST_1";

    private static int i = 1;
    private static int n = 5;
    private static Conference conference1;
    private static Conference conference2;

    @BeforeAll
    static void setUp() {
        controller.register(new StorageController());
        controller.init();
        ConferencesApplication.getErrorChecker().getErrorFlag();

        conference1 = new Conference(DEXA,
                "TK JKU Linz", "ACM");
        conference2 = new Conference(IIWAS,
                "iiWAS", "ACM");
    }

    private static ConferenceEdition createEdition() throws ExecutionException, InterruptedException {
        ConferenceEdition e = new ConferenceEdition(i, i, i, i, i,
                1.0f * i, 1.0f * i, 1.0f * i, "test", "test", "test");
        conferenceEditions.add(e);
        i++;
        return e;
    }

    private static AdditionalMetric createAdditionalMetric() throws ExecutionException, InterruptedException, IOException {
        AdditionalMetric m = new AdditionalMetric(n, n, audioLatency);
        m.setDatapoint(audioLatency.calculateMetric(new String(Files.readAllBytes(Paths.get(TEST_JSON_RESPONSE)))));
        n++;
        return m;
    }

    @AfterAll
    static void tearDown() throws InterruptedException {
        if (!ConferencesApplication.DEBUG) controller.shutdown();
    }

    @Test
    @Order(2)
    void testMapConferenceToDTO() throws ExecutionException, InterruptedException {
        Conference conference = StorageController.getRepository().getConference(DEXA);
        ConferenceDTO dto = StorageController.getMapper().convertToConferenceDTO(conference.getTitle());
        assertEquals(dto.toString(),
                StorageController.getMapper()
                        .convertToConferenceDTO(ElasticReadOperation.retrieveConference(DEXA).getTitle()).toString());
    }

    @Test
    @Order(1)
    void testIngestConference() throws InterruptedException {
        StorageController.indexConference(conference1);
        checkErrorLogs();
        StorageController.indexConference(conference2);
        checkErrorLogs();
    }

    @Test
    @Order(3)
    void testIngestEdition() throws InterruptedException, ExecutionException {
        for (int c = 0; c < 5; c++) {
            StorageController.indexConferenceEdition(createEdition(), StorageController.getRepository().getConference(DEXA));
            checkErrorLogs();
        }
    }

    @Test
    @Order(4)
    void testAdditionalMetricIngestInEdition() throws InterruptedException, ExecutionException, IOException {
        for (int c = 0; c < 5; c++){
            ConferenceEdition edition = createEdition();
            StorageController.indexConferenceEdition(edition, StorageController.getRepository().getConference(DEXA));
            AdditionalMetric additionalMetric = createAdditionalMetric();
            StorageController.indexAdditionalMetric(edition,
                    StorageController.getRepository().getConference(DEXA), additionalMetric);
            StorageController.indexIngestConfiguration(edition, StorageController.getRepository().getConference(DEXA),
                    additionalMetric, additionalMetric.getConfig());
            checkErrorLogs();
        }
    }

    @Test
    @Order(5)
    void testCustomDashboardExportWithAdditionalMetrics() {
        List<DashboardingMetricDTO> dashboardSettings =  new ArrayList<>();
        dashboardSettings.add(new DashboardingMetricDTO(DEXA, ChartType.METRIC.getName(), Operations.AVERAGE.getName(), audioLatency.getTitle()));
        DashboardingUtils.convertToDashboard(StorageController.getRepository().getConference(DEXA), dashboardSettings);
    }

    @Test
    @Order(6)
    void testConferenceRead() throws InterruptedException, ExecutionException {
        Conference conference = ElasticReadOperation.retrieveConference(DEXA);
        System.out.println(conference.toString());
        assertEquals(new String(conference.toString()), new String(StorageController.getRepository().getConference(DEXA).toString()));
    }

    @Test
    @Order(7)
    void testDeletionEdition() throws InterruptedException, ExecutionException {
        if (ConferencesApplication.DEBUG) return;
        StorageController
                .removeConferenceEdition(StorageController.getRepository().getEdition(0));
        assertNull(StorageController.getRepository().getEdition(0));
        assertNull(ElasticReadOperation.retrieveConferenceEdition(0));
    }

    @Test
    @Order(8)
    void testDeletionConference() throws InterruptedException, ExecutionException {
        if (ConferencesApplication.DEBUG) return;
        StorageController
                .removeConference(StorageController.getRepository().getConference(IIWAS));
        assertNull(StorageController.getRepository().getConference(IIWAS));
        assertNull(ElasticReadOperation.retrieveConference(IIWAS));
    }

    @Test
    @Order(9)
    void testDeletionConferenceCascading() throws InterruptedException, ExecutionException {
        if (ConferencesApplication.DEBUG) return;
        StorageController
                .removeConference(StorageController.getRepository().getConference(DEXA));
        assertNull(StorageController.getRepository().getConference(DEXA));
        assertNull(ElasticReadOperation.retrieveConference(DEXA));
        assertNull(ElasticReadOperation.retrieveConferenceEdition(3));
    }

    @Test
    @Order(10)
    void testRetrievalAllConferences() {
        StorageController.fetchConferences();
    }


    private void checkErrorLogs() {
        assertFalse(ConferencesApplication.getErrorChecker().getErrorFlag());
    }
}
