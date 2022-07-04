package masterthesis.conferences.server.dashboarding;

import java.util.List;

public enum Operations {
    MEDIAN("median"),
    MAX("max"),
    MIN("min"),
    AVERAGE("average"),
    COUNT("count");

    private final String name;

    Operations(String name) {
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public static List<String> getOperations(){
        return List.of(MEDIAN.name, MAX.name, MIN.name, AVERAGE.name, COUNT.name);
    }
}
