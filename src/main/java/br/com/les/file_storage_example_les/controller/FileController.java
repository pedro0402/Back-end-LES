package br.com.les.file_storage_example_les.controller;

import br.com.les.file_storage_example_les.data.vo.UploadFileVO;
import br.com.les.file_storage_example_les.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;

@RestController
@RequestMapping("/api/file")
@CrossOrigin(origins = "http://localhost:3000")
public class FileController {

    @Autowired
    private FileStorageService storageService;

    @PostMapping("/uploadFile")
    public UploadFileVO uploadFile(@RequestParam("file")MultipartFile file){
        String fileName = storageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/file/downloadFile/")
                .path(fileName)
                .toUriString();
        return new UploadFileVO(fileName, fileDownloadUri, file.getContentType(), file.getSize());
    }

    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request){

        Resource resource = storageService.loadFileAsResource(fileName);

        String contentType = null;

        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (Exception e){
           e.printStackTrace();
        }
        if (contentType == null){
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

}
