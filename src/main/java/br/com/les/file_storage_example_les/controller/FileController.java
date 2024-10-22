package br.com.les.file_storage_example_les.controller;

import br.com.les.file_storage_example_les.data.vo.UploadFileVO;
import br.com.les.file_storage_example_les.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/file")
@CrossOrigin(origins = "http://localhost:3000")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileStorageService storageService;

    @Value("${summarizer.api.url}")
    private String summarizerApiUrl;

    private Map<String, String> summaryStore = new ConcurrentHashMap<>();

    @PostMapping("/uploadFile")
    public UploadFileVO uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = storageService.storeFile(file);

        String summary = formatSummary(sendFileToSummarizer(file));

        summaryStore.put(fileName, summary);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/file/downloadFile/")
                .path(fileName)
                .toUriString();

        return new UploadFileVO(fileName, fileDownloadUri, file.getContentType(), file.getSize(), summary);
    }

    private String sendFileToSummarizer(MultipartFile file) {
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
                return (String) response.getBody().get("summary");
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

    private File createTempFile(MultipartFile file) throws IOException{
        File tempFile = File.createTempFile("tempFile", ".pdf");
        file.transferTo(tempFile);
        return tempFile;
    }

    private String formatSummary(String summary) {
        String formattedSummary = summary.replaceAll("\\n", " ").replaceAll("\\s+", " ").trim();

        if (formattedSummary.length() > 0) {
            formattedSummary = formattedSummary.replaceAll("[,\\s]*$", "");
        }

        if (!formattedSummary.endsWith(".")) {
            formattedSummary += ".";
        }

        return formattedSummary;
    }

    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        Resource resource = storageService.loadFileAsResource(fileName);

        String contentType = null;

        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/downloadSummary")
    public ResponseEntity<byte[]> downloadSummary(@RequestParam("fileName") String fileName) {
        String summary = getSummaryFromFile(fileName);
        byte[] pdfBytes;

        try {
            pdfBytes = createPdfFromSummary(summary);

            String summaryFileName = "summary_" + fileName;
            storageService.storeSummaryFile(pdfBytes, summaryFileName);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build(); // Retorna erro se algo der errado
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "summary_" + fileName + "\"")
                .body(pdfBytes);
    }

    private String getSummaryFromFile(String fileName) {
        return summaryStore.getOrDefault(fileName, "Resumo não encontrado.");
    }

    public byte[] createPdfFromSummary(String summary) throws DocumentException, IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, outputStream);
        document.open();

        document.add(new Paragraph(summary));

        document.close();
        return outputStream.toByteArray();
    }
}

