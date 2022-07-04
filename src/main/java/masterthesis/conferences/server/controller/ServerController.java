package masterthesis.conferences.server.controller;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import masterthesis.conferences.data.ConferenceRepository;
import masterthesis.conferences.data.MapperService;
import masterthesis.conferences.data.model.Conference;
import masterthesis.conferences.data.model.ConferenceEdition;

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

    private StorageController getStorageController() {
        for (Controller c : controllerList) {
            if (c instanceof StorageController) {
                return (StorageController) c;
            }
        }
        return null;
    }

    public static MapperService getMapper(){
        return getStorageControllerStatic().getMapper();
    }

    public static ConferenceRepository getRepository() {
        return getStorageControllerStatic().getRepository();
    }

    public static ElasticsearchAsyncClient getElastic() {
        return getStorageControllerStatic().getInstance();
    }

    private static StorageController getStorageControllerStatic() {
        assert getInstance() != null;
        return getInstance().getStorageController();
    }

    public static void indexConference(Conference conference) throws InterruptedException {
        getStorageControllerStatic().indexConference(conference);
    }

    public static void indexConferenceEdition(ConferenceEdition edition, Conference conference) throws InterruptedException {
        getStorageControllerStatic().indexConferenceEdition(edition, conference);
    }

    public static void removeConference(Conference conference) throws InterruptedException {
        getStorageControllerStatic().removeConference(conference);
    }

    public static void removeConferenceEdition(ConferenceEdition edition) throws InterruptedException {
        getStorageControllerStatic().removeConferenceEdition(edition);
    }

    public static void fetchConferences() {
        getStorageControllerStatic().fetchConferences();
    }
}
