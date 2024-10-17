package br.com.les.file_storage_example_les;

import br.com.les.file_storage_example_les.config.FileStorageConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
		FileStorageConfig.class
})
public class FileStorageExampleLesApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileStorageExampleLesApplication.class, args);

	}
}
