package masterthesis.conferences.server.rest;

import masterthesis.conferences.data.model.Conference;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Utils {

    private static final String ID = "<CHANGEME-ID>";
    private static final String DASHBOARD = "<CHANGEME-Dashboard>";
    private static final String TITLE = "<CHANGEME-Title>";
    private static final String TITLE_PREFIX = "Conference Overview - ";


    public static String prepareDashboardImport(Conference conference, String json) {
        String jsonReplaced = json.replaceAll(ID, conferenceAsIdList(conference.getConferenceEditionIds()));
        String jsonReplaced2 = jsonReplaced.replaceAll(DASHBOARD, conference.getTitle() + "-dashboard");
        return jsonReplaced2.replaceAll(TITLE, TITLE_PREFIX + conference.getTitle());
    }

    private static String conferenceAsIdList(Set<Integer> conferenceEditionIds) {
        StringBuilder string = new StringBuilder();
        List<Integer> ids = new ArrayList<>(conferenceEditionIds);
        for (int i = 0; i < conferenceEditionIds.size(); i++) {
            if (i != 0) {
                string.append(" or ");
            }
            string.append(ids.get(i));
        }
        return string.toString();
    }
}
