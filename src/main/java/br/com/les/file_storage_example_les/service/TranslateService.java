package br.com.les.file_storage_example_les.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Service
public class TranslateService {

    private final String translateApiUrl;

    @Autowired
    public TranslateService(@Value("${translate.api.url}") String translateApiUrl) {
        this.translateApiUrl = translateApiUrl;
    }

    public String sendFileToTranslator(MultipartFile file, String language) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        File tempFile = null;

        try {
            tempFile = createTempFile(file);
            body.add("file", new org.springframework.core.io.FileSystemResource(tempFile));
            body.add("language", language);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.exchange(translateApiUrl, HttpMethod.POST, requestEntity, Map.class);

            if (response.getBody() != null && response.getStatusCode().is2xxSuccessful()) {
                return (String) response.getBody().get("translated_text");
            } else {
                return "Failed to retrieve translation.";
            }
        } catch (Exception e) {
            return "Error during API call: " + e.getMessage();
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    private File createTempFile(MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("tempFile", ".pdf");
        file.transferTo(tempFile);
        return tempFile;
    }


}
