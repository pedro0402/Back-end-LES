package br.com.les.file_storage_example_les.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
public class SummarizerClientService {

    @Value("${summarizer.api.url")
    private String summarizerApiUrl;

    public String summarizeText(String text, double ratio) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("text", text);
        requestBody.put("ratio", ratio);

        HttpEntity<Map<String, Object>> requestEntity = createRequestEntity(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    summarizerApiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    Map.class
            );

            Map<String, Object> responseBody = response.getBody();
            return (String) responseBody.get("summary");
        } catch (Exception e) {
            e.printStackTrace();
            return "Error during summarization: " + e.getMessage();
        }
    }

    private HttpEntity<Map<String, Object>> createRequestEntity(Map<String,Object> requestBody, HttpHeaders headers){
        return new HttpEntity<>(requestBody, headers);
    }

}