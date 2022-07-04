package masterthesis.conferences.elastic.datamodel;

import masterthesis.conferences.ConferencesApplication;
import masterthesis.conferences.data.dto.ConferenceDTO;
import masterthesis.conferences.data.model.Conference;
import masterthesis.conferences.data.model.ConferenceEdition;
import masterthesis.conferences.server.controller.ServerController;
import masterthesis.conferences.server.controller.StorageController;
import masterthesis.conferences.server.rest.storage.ElasticSearchOperations;
import org.junit.jupiter.api.*;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DatamodelTests {

    private static final ServerController controller = new ServerController();
    private static final Set<ConferenceEdition> conferenceEditions = new HashSet<>();

    private static final String DEXA = "DEXA_UNIT_TEST_1";
    private static final String IIWAS = "iiWAS & MoMM_UNIT_TEST_1";

    private static int i = 1;
    private static Conference conference1;
    private static Conference conference2;

    @BeforeAll
    static void setUp() {
        controller.register(new StorageController());
        controller.init();

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

    @AfterAll
    static void tearDown() throws InterruptedException {
        if (!ConferencesApplication.DEBUG) controller.shutdown();
    }

    @Test
    @Order(2)
    void testMapConferenceToDTO() {
        Conference conference = ServerController.getRepository().getConference(DEXA);
        ConferenceDTO dto = ServerController.getMapper().convertToConferenceDTO(conference.getTitle());
        assertEquals(dto.toString(), new ConferenceDTO(DEXA, "TK JKU Linz", "ACM", Set.of()).toString());
    }

    @Test
    @Order(1)
    void testIngestConference() throws InterruptedException {
        ServerController.indexConference(conference1);
        checkErrorLogs();
        ServerController.indexConference(conference2);
        checkErrorLogs();
    }

    @Test
    @Order(3)
    void testIngestEdition() throws InterruptedException, ExecutionException {
        for (int c = 0; c < 5; c++) {
            ServerController.indexConferenceEdition(createEdition(), ServerController.getRepository().getConference(DEXA));
            checkErrorLogs();
        }
    }

    @Test
    @Order(4)
    void testConferenceRead() throws InterruptedException, ExecutionException {
        Conference conference = ElasticSearchOperations.retrieveConference(DEXA);
        System.out.println(conference.toString());
        assertEquals(conference.toString(), ServerController.getRepository().getConference(DEXA).toString());
    }

    @Test
    @Order(5)
    void testDeletionEdition() throws InterruptedException, ExecutionException {
        ServerController
                .removeConferenceEdition(ServerController.getRepository().getEdition(0));
        assertNull(ServerController.getRepository().getEdition(0));
        assertNull(ElasticSearchOperations.retrieveConferenceEdition(0));
    }

    @Test
    @Order(6)
    void testDeletionConference() throws InterruptedException, ExecutionException {
        ServerController
                .removeConference(ServerController.getRepository().getConference(IIWAS));
        assertNull(ServerController.getRepository().getConference(IIWAS));
        assertNull(ElasticSearchOperations.retrieveConference(IIWAS));
    }

    @Test
    @Order(7)
    void testDeletionConferenceCascading() throws InterruptedException, ExecutionException {
        ServerController
                .removeConference(ServerController.getRepository().getConference(DEXA));
        assertNull(ServerController.getRepository().getConference(DEXA));
        assertNull(ElasticSearchOperations.retrieveConference(DEXA));
        assertNull(ElasticSearchOperations.retrieveConferenceEdition(3));
    }

    @Test
    @Order(8)
    void testRetrievalAllConferences() {
        ServerController.fetchConferences();
    }


    private void checkErrorLogs() {
        assertFalse(ConferencesApplication.getErrorChecker().getErrorFlag());
    }
}
