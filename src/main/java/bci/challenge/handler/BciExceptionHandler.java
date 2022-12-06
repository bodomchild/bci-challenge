package bci.challenge.handler;

import bci.challenge.dto.ErrorsDTO;
import bci.challenge.exception.BciException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class BciExceptionHandler {

    @ExceptionHandler(BciException.class)
    public ResponseEntity<ErrorsDTO> handleBciException(BciException e) {
        ErrorsDTO.ErrorDTO error = ErrorsDTO.ErrorDTO.builder()
                .code(e.getHttpStatus())
                .detail(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        ErrorsDTO errors = ErrorsDTO.builder()
                .error(Collections.singletonList(error))
                .build();

        return ResponseEntity.status(e.getHttpStatus()).body(errors);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorsDTO> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<ErrorsDTO.ErrorDTO> error = e.getFieldErrors().stream()
                .map(err -> ErrorsDTO.ErrorDTO.builder()
                        .code(HttpStatus.BAD_REQUEST.value())
                        .detail(err.getDefaultMessage())
                        .timestamp(LocalDateTime.now())
                        .build())
                .collect(Collectors.toList());

        ErrorsDTO errors = ErrorsDTO.builder()
                .error(error)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorsDTO> handleException(Exception e) {
        ErrorsDTO.ErrorDTO error = ErrorsDTO.ErrorDTO.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .detail(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        ErrorsDTO errors = ErrorsDTO.builder()
                .error(Collections.singletonList(error))
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(errors);
    }

}
