package masterthesis.conferences.elastic.datamodel;

import masterthesis.conferences.ConferencesApplication;
import masterthesis.conferences.data.dto.ConferenceDTO;
import masterthesis.conferences.data.model.Conference;
import masterthesis.conferences.data.model.ConferenceEdition;
import masterthesis.conferences.server.controller.ServerController;
import masterthesis.conferences.server.rest.storage.ElasticSearchOperations;
import masterthesis.conferences.server.rest.storage.StorageController;
import org.junit.jupiter.api.*;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DatamodelTests {

    private static final ServerController controller = new ServerController();
    private static final Set<ConferenceEdition> conferenceEditions = new HashSet<>();

    private static final String DEXA = "DEXA and related conferences";
    private static final String IIWAS = "iiWAS & MoMM";

    private static int i = 1;

    @BeforeAll
    static void setUp() throws ExecutionException, InterruptedException {
        controller.register(new StorageController());
        controller.init();

        Conference conference1 = new Conference(DEXA,
                "TK JKU Linz", "ACM");
        Conference conference2 = new Conference(IIWAS,
                "iiWAS", "ACM");
        StorageController.getRepository().addConference(conference1);
        StorageController.getRepository().addConference(conference2);
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
    @Order(1)
    void testMapConferenceToDTO() {
        Conference conference = StorageController.getRepository().getConference(DEXA);
        ConferenceDTO dto = StorageController.getMapper().convertToConferenceDTO(conference.getTitle());
        assertTrue(dto.toString()
                .equals(new ConferenceDTO(DEXA, "TK JKU Linz", "ACM", Set.of()).toString()));
    }

    @Test
    @Order(2)
    void testIngestConference() throws InterruptedException {
        controller.getStorageController().indexConference(StorageController.getRepository().getConference(DEXA));
        checkErrorLogs();
        controller.getStorageController().indexConference(StorageController.getRepository().getConference(IIWAS));
        checkErrorLogs();
    }

    @Test
    @Order(3)
    void testIngestEdition() throws InterruptedException, ExecutionException {
        for (int c = 0; c < 5; c++) {
            controller.getStorageController()
                    .indexConferenceEdition(createEdition(), StorageController.getRepository().getConference(DEXA));
            checkErrorLogs();
        }
    }

    @Test
    @Order(4)
    void testConferenceRead() throws InterruptedException, ExecutionException {
        Conference conference = ElasticSearchOperations.retrieveConference(DEXA);
        System.out.println(conference.toString());
        assertTrue(conference.toString().equals(StorageController.getRepository().getConference(DEXA).toString()));
    }

    private void checkErrorLogs() {
        assertFalse(ConferencesApplication.getErrorChecker().getErrorFlag());
    }
}
