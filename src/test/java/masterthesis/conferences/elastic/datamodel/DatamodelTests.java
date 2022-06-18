package masterthesis.conferences.elastic.datamodel;

import masterthesis.conferences.ConferencesApplication;
import masterthesis.conferences.data.dto.ConferenceDTO;
import masterthesis.conferences.server.controller.ServerController;
import masterthesis.conferences.server.controller.storage.rest.StorageController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class DatamodelTests {

    private static final ServerController controller = new ServerController();

    @BeforeEach
    void setUp() {
        controller.register(new StorageController());
        controller.init();
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        controller.shutdown();
    }

    @Test
    void ingestTestConference() throws InterruptedException {
        ConferenceDTO conference = new ConferenceDTO("DEXA and related conferences",
                "TK JKU Linz", "ACM",
                Set.of(1, 2));
        controller.getStorageController().indexConference(conference);
        checkErrorLogs();
        ConferenceDTO conference2 = new ConferenceDTO("iiWAS & MoMM",
                "iiWAS", "ACM", Set.of(3, 4));
        controller.getStorageController().indexConference(conference2);
        checkErrorLogs();
    }

    private void checkErrorLogs() {
        assertFalse(ConferencesApplication.getErrorChecker().getErrorFlag());
    }
}
