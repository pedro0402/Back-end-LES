package br.com.les.file_storage_example_les.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class FileStorageException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public FileStorageException(String exception){
        super(exception);
    }

    public FileStorageException(String exception, Throwable cause){
        super(exception, cause);
    }
}
