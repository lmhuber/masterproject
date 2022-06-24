package masterthesis.conferences;

import masterthesis.conferences.server.controller.ErrorFilter;
import masterthesis.conferences.server.controller.ServerController;
import masterthesis.conferences.server.rest.storage.StorageController;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"masterthesis.conferences.server.controller", "masterthesis.conferences.server.rest.service"})
public class ConferencesApplication {
	private static ServerController controller;
	private static boolean running = false;

	private static final Log logger = LogFactory.getLog(ConferencesApplication.class);
	private static final ErrorFilter errorChecker = new ErrorFilter();
	public static final boolean DEBUG = true;


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
		controller.register(StorageController.getControllerInstance());

		controller.init();
	}

	public static Log getLogger() {
		return logger;
	}

	public static ErrorFilter getErrorChecker() {
		return errorChecker;
	}

}
