package masterthesis.conferences.server.controller;

import masterthesis.conferences.server.rest.storage.StorageController;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ServerController implements Controller {

    private final List<Controller> controllerList = new ArrayList<>();

    public void register(Controller controller) {
        controllerList.add(controller);
    }

    public void deregister(Controller controller) {
        controllerList.remove(controller);
    }

    public void deregister(Iterator<Controller> iterator) {
        iterator.remove();
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
    public void shutdown() throws InterruptedException {
        Iterator<Controller> iterator = controllerList.iterator();
        while (iterator.hasNext()) {
            Controller c = iterator.next();
            c.shutdown();
            deregister(iterator);
        }
    }

    public StorageController getStorageController() {
        for (Controller c : controllerList) {
            if (c instanceof StorageController) {
                return (StorageController) c;
            }
        }
        return null;
    }
}
