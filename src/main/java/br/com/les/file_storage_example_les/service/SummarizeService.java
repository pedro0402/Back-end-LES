package br.com.les.file_storage_example_les.service;
import br.com.les.file_storage_example_les.controller.FileController;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SummarizeService {

    private final String summarizerApiUrl;

    public Map<String, String> summaryStore = new ConcurrentHashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    public SummarizeService(@Value("${summarizer.api.url}") String summarizeApiUrl) {
        this.summarizerApiUrl = summarizeApiUrl;
    }

    private File createTempFile(MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("tempFile", ".pdf");
        file.transferTo(tempFile);
        return tempFile;
    }

    public String getSummaryFromFile(String fileName) {
        return summaryStore.getOrDefault(fileName, "Resumo não encontrado.");
    }

    public byte[] createPdfFromSummary(String summary, String titleText) throws DocumentException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);

        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
        Paragraph title = new Paragraph(titleText, titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10);
        document.add(title);

        LineSeparator separator = new LineSeparator();
        separator.setPercentage(100);
        separator.setLineColor(BaseColor.LIGHT_GRAY);
        document.add(new Chunk(separator));

        Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.DARK_GRAY);
        Paragraph content = new Paragraph(summary, contentFont);
        content.setAlignment(Element.ALIGN_JUSTIFIED);
        content.setSpacingBefore(10);
        content.setSpacingAfter(10);
        document.add(content);

        document.close();
        return outputStream.toByteArray();
    }


    public String sendFileToSummarizer(MultipartFile file) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        File tempFile = null;
        try {
            tempFile = createTempFile(file);
            body.add("file", new org.springframework.core.io.FileSystemResource(tempFile));
            body.add("ratio", 0.2);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    summarizerApiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    Map.class
            );

            if (response.getBody() != null && response.getStatusCode().is2xxSuccessful()) {
                String summary = (String) response.getBody().get("summary");
                return TextFormatter.formatText(summary);
            } else {
                logger.error("Failed to retrieve summary: Empty response or unsuccessful status code");
                return "Failed to retrieve summary.";
            }

        } catch (Exception e) {
            logger.error("Error during summarization", e);
            return "Error during summarization: " + e.getMessage();
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

}
