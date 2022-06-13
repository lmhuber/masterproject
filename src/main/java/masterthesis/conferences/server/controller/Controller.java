package masterthesis.conferences.server.controller;

import java.util.concurrent.ExecutionException;

public interface Controller {
    void init() throws ExecutionException, InterruptedException;

    void shutdown();
}
