package masterthesis.conferences.server.controller;

import java.util.concurrent.ExecutionException;

public interface Controller {
    /**
     * Initializes the controller
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    void init() throws ExecutionException, InterruptedException;

    /**
     * Stops and frees everything needed to run the controller to properly shut it down
     * @throws InterruptedException
     */
    void shutdown() throws InterruptedException;
}
