package masterthesis.conferences.server.controller;

import java.util.ArrayList;
import java.util.List;

public class ServerController implements Controller {

    private final List<Controller> controllerList = new ArrayList<>();

    public void register(Controller controller) {
        controllerList.add(controller);
    }

    public void deregister(Controller controller) {
        controllerList.remove(controller);
    }

    @Override
    public synchronized void init() {
        controllerList.forEach(c -> {
            try {
                c.init();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void shutdown() {
        controllerList.forEach(Controller::shutdown);
    }
}
