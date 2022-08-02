package masterthesis.conferences.data.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties("qos")
public class QoSConfig {
    private List<String> meetingIdList;

    public List<String> getMeetingIdList() {
        return meetingIdList;
    }

}
