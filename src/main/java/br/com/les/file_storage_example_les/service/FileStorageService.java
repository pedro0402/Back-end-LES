package br.com.les.file_storage_example_les.service;

import br.com.les.file_storage_example_les.config.FileStorageConfig;
import br.com.les.file_storage_example_les.exception.FileNotFoundException;
import br.com.les.file_storage_example_les.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    public FileStorageService(FileStorageConfig fileStorageConfig) {
        this.fileStorageLocation = Paths.get(fileStorageConfig.getUploadDir())
                .toAbsolutePath().normalize();

        createDirectoryIfNotExists();
    }

    private void createDirectoryIfNotExists() {
        try {
            if (Files.notExists(this.fileStorageLocation)) {
                Files.createDirectories(this.fileStorageLocation);
            }
        } catch (Exception e) {
            throw new FileStorageException("Não é possível criar o diretório de upload", e);
        }
    }

    public String storeFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        validateFile(fileName, file.getContentType());

        try {
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (Exception e) {
            throw new FileStorageException("Não é possível armazenar o arquivo " + fileName + " tente novamente.", e);
        }
    }

    private void validateFile(String fileName, String contentType){
        if (!"application/pdf".equals(contentType)) {
            throw new FileStorageException("Apenas arquivos PDF são permitidos. Tipo de arquivo: " + contentType);
        }

        if (!fileName.toLowerCase().endsWith(".pdf")) {
            throw new FileStorageException("O arquivo " + fileName + " não é do tipo PDF. Apenas arquivos PDF!");
        }

        if (fileName.contains("..")) {
            throw new FileStorageException("Nome do arquivo possui uma sequência errada! " + fileName);
        }

    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else throw new FileNotFoundException("Arquivo não encontrado" + fileName);
        } catch (Exception e) {
            throw new FileNotFoundException("Arquivo não encontrado" + fileName, e);
        }
    }

    public Resource loadTranslationFileAsResource(String fileName) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("File not found: " + fileName);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("File not found: " + fileName, e);
        }
    }


    public String storeSummaryFile(byte[] pdfBytes, String fileName) {
        try {
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.write(targetLocation, pdfBytes);
            return fileName;
        } catch (Exception e) {
            throw new FileStorageException("Não é possível armazenar o arquivo resumo " + fileName + " tente novamente.", e);
        }
    }
}
