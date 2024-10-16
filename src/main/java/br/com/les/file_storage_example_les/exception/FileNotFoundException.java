package br.com.les.file_storage_example_les.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FileNotFoundException extends RuntimeException{

    @Serial
    private static final long serialVersionUID = 1L;

    public FileNotFoundException(String exception){
        super(exception);
    }

    public FileNotFoundException(String exception, Throwable cause){
        super(exception, cause);
    }
}
