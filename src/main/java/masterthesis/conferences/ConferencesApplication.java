package masterthesis.conferences;

import masterthesis.conferences.server.controller.ServerController;
import masterthesis.conferences.server.controller.StorageController;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"masterthesis.conferences.server.controller", "masterthesis.conferences.server.rest.service"})
@EnableScheduling
public class ConferencesApplication {
	private static ServerController controller;
	private static boolean running = true;

	private static final Log logger = LogFactory.getLog(ConferencesApplication.class);
	private static final ErrorFilter errorChecker = new ErrorFilter();
	public static final boolean DEBUG = true;


	public static void main(String[] args) throws InterruptedException {
		initServer();
		SpringApplication.run(ConferencesApplication.class, args);

		while (running) {
			Thread.sleep(100);
		}
		controller.shutdown();
		controller.deregister();
	}

	private static void initServer() {
		controller = new ServerController();
		controller.register(new StorageController());
		controller.init();
	}

	public static Log getLogger() {
		return logger;
	}

	public static ErrorFilter getErrorChecker() {
		return errorChecker;
	}
}
