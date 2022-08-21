package masterthesis.conferences.elastic.datamodel;

import masterthesis.conferences.ConferencesApplication;
import masterthesis.conferences.data.metrics.zoom.AudioLatency;
import masterthesis.conferences.data.model.AdditionalMetric;
import masterthesis.conferences.data.model.Conference;
import masterthesis.conferences.data.model.ConferenceEdition;
import masterthesis.conferences.data.model.dto.ConferenceDTO;
import masterthesis.conferences.data.model.dto.DashboardingMetricDTO;
import masterthesis.conferences.server.controller.ServerController;
import masterthesis.conferences.server.controller.storage.StorageController;
import masterthesis.conferences.server.dashboarding.ChartType;
import masterthesis.conferences.server.dashboarding.DashboardingUtils;
import masterthesis.conferences.server.dashboarding.Operations;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static masterthesis.conferences.metrics.APIMetricTests.TEST_JSON_RESPONSE;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Disabled("Can be used instead of ingest with sample data, see ConferencesApplication.LOAD_SAMPLES")
public class DatamodelTests {

    private static final ServerController controller = new ServerController();
    private final Set<ConferenceEdition> conferenceEditions = new HashSet<>();
    private final StorageController storageController = StorageController.getInstance();
    private final AudioLatency audioLatency = new AudioLatency();

    private static final int NUMBER_EDITIONS = 5;
    private static final String DEXA = "DEXA_UNIT_TEST_1";
    private static final String IIWAS = "iiWAS & MoMM_UNIT_TEST_1";

    private static int i = 1;
    private static int n = 5;
    private static Conference conference1;
    private static Conference conference2;

    @BeforeAll
    static void setUp() {
        controller.register(StorageController.getInstance());
        controller.init();
        ConferencesApplication.getErrorChecker().getErrorFlag();

        conference1 = new Conference(DEXA,
                "TK JKU Linz", "ACM");
        conference2 = new Conference(IIWAS,
                "iiWAS", "ACM");
    }

    private ConferenceEdition createEdition() {
        ConferenceEdition e = new ConferenceEdition(i, i, i, i, i, i,
                1.0f * i, 1.0f * i, 1.0f * i, "test", "test", "test");
        conferenceEditions.add(e);
        i++;
        return e;
    }

    private AdditionalMetric createAdditionalMetric() throws IOException {
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
    void testMapConferenceToDTO() {
        Conference conference = storageController.getConference(DEXA);
        ConferenceDTO dto = StorageController.getMapper().convertToConferenceDTO(conference.getTitle());
        assertEquals(dto.toString(),
                StorageController.getMapper()
                        .convertToConferenceDTO(DEXA).toString());
    }

    @Test
    @Order(1)
    void testIngestConference() throws InterruptedException {
        storageController.indexConference(conference1);
        checkErrorLogs();
        storageController.indexConference(conference2);
        checkErrorLogs();
    }

    @Test
    @Order(3)
    void testIngestEdition() throws InterruptedException {
        for (int c = 0; c < NUMBER_EDITIONS; c++) {
            storageController.indexConferenceEdition(createEdition(), storageController.getConference(DEXA));
            assertEquals(conferenceEditions.size(), c);
            checkErrorLogs();
        }
    }

    @Test
    @Order(4)
    void testAdditionalMetricIngestInEdition() throws InterruptedException, IOException {
        for (int c = 0; c < NUMBER_EDITIONS; c++){
            assertEquals(conferenceEditions.size(), NUMBER_EDITIONS + c);
            ConferenceEdition edition = createEdition();
            storageController.indexConferenceEdition(edition, storageController.getConference(DEXA));
            AdditionalMetric additionalMetric = createAdditionalMetric();
            storageController.indexAdditionalMetric(edition,
                    storageController.getConference(DEXA), additionalMetric);
            storageController.indexIngestConfiguration(edition, storageController.getConference(DEXA),
                    additionalMetric, additionalMetric.getConfig());
            checkErrorLogs();
        }
    }

    @Test
    @Order(5)
    void testCustomDashboardExportWithAdditionalMetrics() {
        List<DashboardingMetricDTO> dashboardSettings =  new ArrayList<>();
        dashboardSettings.add(new DashboardingMetricDTO(DEXA, ChartType.METRIC.getName(), Operations.AVERAGE.getName(), audioLatency.getTitle(), true));
        DashboardingUtils.convertToDashboard(storageController.getConference(DEXA), dashboardSettings);
    }

    @Test
    @Order(6)
    void testConferenceRead() {
        System.out.println(conference1.toString());
        assertEquals(conference1.toString(), storageController.getConference(DEXA).toString());
    }

    @Test
    @Order(7)
    void testDeletionEdition() throws InterruptedException {
        if (ConferencesApplication.DEBUG) return;
        storageController
                .removeConferenceEdition(storageController.getEdition(0));
        assertNull(storageController.getEdition(0));
    }

    @Test
    @Order(8)
    void testDeletionConference() throws InterruptedException {
        if (ConferencesApplication.DEBUG) return;
        storageController
                .removeConference(storageController.getConference(IIWAS));
        assertNull(storageController.getConference(IIWAS));
    }

    @Test
    @Order(9)
    void testDeletionConferenceCascading() throws InterruptedException {
        if (ConferencesApplication.DEBUG) return;
        storageController
                .removeConference(storageController.getConference(DEXA));
        assertNull(storageController.getConference(DEXA));
        storageController.fetchConferences();
        assertNull(storageController.getConference(DEXA));
    }

    @Test
    @Order(10)
    void testRetrievalAllConferences() {
        storageController.fetchConferences();
    }


    private void checkErrorLogs() {
        assertFalse(ConferencesApplication.getErrorChecker().getErrorFlag());
    }
}
