package br.com.les.file_storage_example_les.controller;

import br.com.les.file_storage_example_les.data.vo.UploadFileVO;
import br.com.les.file_storage_example_les.service.SummarizeService;
import br.com.les.file_storage_example_les.service.TranslateService;
import br.com.les.file_storage_example_les.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/file")
@CrossOrigin(origins = "http://localhost:3000")
public class FileController {

    @Autowired
    private FileStorageService storageService;

    @Autowired
    private TranslateService translateService;

    @Autowired
    private SummarizeService summarizeService;


    @PostMapping("/translateFile")
    public ResponseEntity<byte[]> translateFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("language") String language) {

        String translatedText = translateService.sendFileToTranslator(file, language);

        if (translatedText == null || translatedText.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve translation.".getBytes());
        }

        byte[] pdfBytes;
        try {
            pdfBytes = summarizeService.createPdfFromSummary(translatedText);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"translated_file.pdf\"")
                .body(pdfBytes);
    }



    // para usar -> http://localhost:8081/api/file/downloadSummary?fileName=example.pdf
    @GetMapping("/downloadSummary")
    public ResponseEntity<byte[]> downloadSummary(@RequestParam("fileName") String fileName) {
        String summary = summarizeService.getSummaryFromFile(fileName);
        byte[] pdfBytes;

        try {
            pdfBytes = summarizeService.createPdfFromSummary(summary);

            String summaryFileName = "summary_" + fileName;
            storageService.storeSummaryFile(pdfBytes, summaryFileName);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + "summary_" + fileName + "\"")
                .body(pdfBytes);
    }

    @PostMapping("/uploadFile")
    public UploadFileVO uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = storageService.storeFile(file);

        String summary = summarizeService.formatSummary(summarizeService.sendFileToSummarizer(file));
        summarizeService.summaryStore.put(fileName, summary);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/file/downloadSummary")
                .queryParam("fileName", fileName)
                .toUriString();

        return new UploadFileVO(fileName, fileDownloadUri, file.getContentType(), file.getSize(), summary);
    }

}