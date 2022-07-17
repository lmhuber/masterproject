package masterthesis.conferences.server.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/qosmetrics")
public class QualityOfServiceMetrics {

    @GetMapping
    public ResponseEntity<?> getQOSMetrics(String meetingId) {
        try {
            String uri="https://apimocha.com/conferences/metrics/meetings/{meetingId}/participants/qos";
            RestTemplate restTemplate = new RestTemplate();
            Map<String, String> params = new HashMap<>();
            params.put("meetingId", meetingId);

            // create headers
            HttpHeaders headers = new HttpHeaders();

            // set content and accept headers
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            // build the request
            HttpEntity request = new HttpEntity(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    request,
                    String.class,
                    params
            );
            return response;
        }catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}