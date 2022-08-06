package masterthesis.conferences;

import masterthesis.conferences.server.controller.ServerController;
import masterthesis.conferences.server.controller.storage.StorageController;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"masterthesis.conferences.server.controller", "masterthesis.conferences.server.rest.service"})
@EnableScheduling
public class ConferencesApplication {
	private static ServerController controller;

	private static final Log logger = LogFactory.getLog(ConferencesApplication.class);
	private static final ErrorFilter errorChecker = new ErrorFilter();
	public static final boolean DEBUG = false;


	public static void main(String[] args) throws InterruptedException {
		initServer();
		SpringApplication.run(ConferencesApplication.class, args);

		boolean running = true;
		while (running) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				running = false;
			}
		}
		controller.shutdown();
		controller.deregister();
	}

	private static void initServer() {
		controller = new ServerController();
		controller.register(StorageController.getInstance());
		controller.init();
	}

	public static Log getLogger() {
		return logger;
	}

	public static ErrorFilter getErrorChecker() {
		return errorChecker;
	}
}
