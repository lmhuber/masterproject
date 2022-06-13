package masterthesis.conferences;

import masterthesis.conferences.server.controller.ServerController;
import masterthesis.conferences.server.controller.storage.StorageController;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConferencesApplication {
	private static ServerController controller;
	private static final Log logger = LogFactory.getLog(ConferencesApplication.class);
	private static boolean running = false;

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(ConferencesApplication.class, args);
		initServer();

		while (running) {
			Thread.sleep(100);
		}
		controller.shutdown();
	}

	private static void initServer() {
		controller = new ServerController();
		controller.register(new StorageController());

		controller.init();
	}

	public static Log getLogger() {
		return logger;
	}

}
