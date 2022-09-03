package masterthesis.conferences.elastic.datamodel;

import masterthesis.conferences.ConferencesApplication;
import masterthesis.conferences.data.metrics.zoom.AudioLatency;
import masterthesis.conferences.data.model.AdditionalMetric;
import masterthesis.conferences.data.model.Conference;
import masterthesis.conferences.data.model.ConferenceEdition;
import masterthesis.conferences.data.model.dto.ConferenceDTO;
import masterthesis.conferences.data.model.dto.DashboardingMetricDTO;
import masterthesis.conferences.server.controller.ServerController;
import masterthesis.conferences.server.controller.storage.MapperService;
import masterthesis.conferences.server.controller.storage.StorageController;
import masterthesis.conferences.server.dashboarding.ChartType;
import masterthesis.conferences.server.dashboarding.DashboardingUtils;
import masterthesis.conferences.server.dashboarding.Operations;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static masterthesis.conferences.metrics.APIMetricTests.TEST_JSON_RESPONSE;
import static org.junit.jupiter.api.Assertions.*;

public class DatamodelTests {

    private static final ServerController controller = Mockito.mock(ServerController.class);
    private static final MapperService mapper = Mockito.mock(MapperService.class);
    private static final HashSet conferenceEditions = Mockito.mock(HashSet.class);
    private static final StorageController storageController = Mockito.mock(StorageController.class);
    private final AudioLatency audioLatency = new AudioLatency();

    private static final int NUMBER_EDITIONS = 5;
    private static final String DEXA = "DEXA_UNIT_TEST_1";
    private static final String IIWAS = "iiWAS & MoMM_UNIT_TEST_1";

    private static int i = 1;
    private static int n = 5;
    private static Conference conference1;
    private static Conference conference2;

    @BeforeAll
    static void setUp() throws InterruptedException {
        conference1 = new Conference(DEXA,
                "TK JKU Linz", "ACM");
        conference2 = new Conference(IIWAS,
                "iiWAS", "ACM");
        Mockito.when(mapper.convertToConferenceDTO(DEXA)).thenReturn(new ConferenceDTO(conference1.getTitle(),
                conference1.getOrganization(), conference1.getPublisher(), new HashSet<>()));
        Mockito.when(storageController.getConference(DEXA)).thenReturn(conference1);
        Mockito.doAnswer((Answer<Object>) invocationOnMock ->
                Mockito.when(storageController.getConference(DEXA)).thenReturn(null))
                .when(storageController).removeConference(conference1);

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
    void testMapConferenceToDTO() {
        Mockito.when(storageController.getConference(DEXA)).thenReturn(conference1);
        Conference conference = storageController.getConference(DEXA);
        ConferenceDTO dto = mapper.convertToConferenceDTO(conference.getTitle());
        assertEquals(dto.toString(),
                mapper.convertToConferenceDTO(DEXA).toString());
    }

    @Test
    void testIngestConference() throws InterruptedException {
        storageController.indexConference(conference1);
        checkErrorLogs();
        storageController.indexConference(conference2);
        checkErrorLogs();
    }

    @Test
    void testIngestEdition() throws InterruptedException {
        Mockito.when(conferenceEditions.size()).thenReturn(0,1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        for (int c = 0; c < NUMBER_EDITIONS; c++) {
            storageController.indexConferenceEdition(createEdition(), storageController.getConference(DEXA));
            assertEquals(conferenceEditions.size(), c);
            checkErrorLogs();
        }
    }

    @Test
    void testAdditionalMetricIngestInEdition() throws InterruptedException, IOException {
        Mockito.when(conferenceEditions.size()).thenReturn(5, 6, 7, 8, 9, 10);
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
    void testCustomDashboardExportWithAdditionalMetrics() {
        Mockito.when(storageController.getConference(DEXA)).thenReturn(conference1);
        List<DashboardingMetricDTO> dashboardSettings =  new ArrayList<>();
        dashboardSettings.add(new DashboardingMetricDTO(DEXA, ChartType.METRIC.getName(), Operations.AVERAGE.getName(), audioLatency.getTitle(), true));
        DashboardingUtils.convertToDashboard(storageController.getConference(DEXA), dashboardSettings);
    }

    @Test
    void testConferenceRead() {
        System.out.println(conference1.toString());
        assertEquals(conference1.toString(), storageController.getConference(DEXA).toString());
    }

    @Test
    void testDeletionEdition() throws InterruptedException {
        if (ConferencesApplication.DEBUG) return;
        storageController
                .removeConferenceEdition(storageController.getEdition(0));
        assertNull(storageController.getEdition(0));
    }

    @Test
    void testDeletionConference() throws InterruptedException {
        if (ConferencesApplication.DEBUG) return;
        storageController
                .removeConference(storageController.getConference(IIWAS));
        assertNull(storageController.getConference(IIWAS));
    }

    @Test
    void testDeletionConferenceCascading() throws InterruptedException {
        if (ConferencesApplication.DEBUG) return;
        storageController
                .removeConference(storageController.getConference(DEXA));
        assertNull(storageController.getConference(DEXA));
        storageController.fetchConferences();
        assertNull(storageController.getConference(DEXA));
    }

    @Test
    void testRetrievalAllConferences() {
        storageController.fetchConferences();
    }


    private void checkErrorLogs() {
        assertFalse(ConferencesApplication.getErrorChecker().getErrorFlag());
    }
}
