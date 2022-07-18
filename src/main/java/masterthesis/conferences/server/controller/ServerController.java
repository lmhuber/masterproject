package masterthesis.conferences.server.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ServerController implements Controller {

    private static ServerController instance = null;

    private final List<Controller> controllerList = new ArrayList<>();

    public void register(Controller controller) {
        controllerList.add(controller);
    }

    public void deregister() {
        controllerList.clear();
    }

    public void deregister(Iterator<Controller> iterator) {
        iterator.remove();
    }

    private static ServerController getInstance() {
        if (instance == null) return null;
        return instance;
    }

    @Override
    public synchronized void init() {
        instance = this;
        controllerList.forEach(c -> {
            try {
                c.init();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void shutdown() throws InterruptedException {
        Iterator<Controller> iterator = controllerList.iterator();
        while (iterator.hasNext()) {
            Controller c = iterator.next();
            c.shutdown();
            deregister(iterator);
        }
    }
}
