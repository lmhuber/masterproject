package masterthesis.conferences.elastic.datamodel;

import masterthesis.conferences.data.dto.ConferenceDTO;
import masterthesis.conferences.data.dto.ConferenceEditionDTO;
import masterthesis.conferences.server.controller.ServerController;
import masterthesis.conferences.server.controller.storage.StorageController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

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
    void ingestTestConference() {
        ConferenceDTO conference = new ConferenceDTO("DEXA and related conferences",
                "TK JKU Linz", "ACM",
                new ConferenceEditionDTO(1, 1, 1, 11, 1, 1,
                        1, 1, 1, "", "", "", new HashMap<>()),
                new ConferenceEditionDTO(2, 1, 1, 11, 1, 1,
                        1, 1, 1, "", "", "", new HashMap<>()));
        controller.getStorageController().indexConference(conference);
        ConferenceDTO conference2 = new ConferenceDTO("iiWAS & MoMM",
                "iiWAS", "ACM",
                new ConferenceEditionDTO(3, 1, 1, 11, 1, 1,
                        1, 1, 1, "", "", "", new HashMap<>()),
                new ConferenceEditionDTO(4, 1, 1, 11, 1, 1,
                        1, 1, 1, "", "", "", new HashMap<>()));
        controller.getStorageController().indexConference(conference2);

    }
}
