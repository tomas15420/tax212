package eu.tmach.tax212.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {

        List<FieldErrorDetail> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> FieldErrorDetail.builder()
                        .field(err.getField())
                        .message(err.getDefaultMessage())
                        .build()
                )
                .toList();

        return ResponseEntity.badRequest().body(
                ValidationErrorResponse.builder()
                        .status(400)
                        .message("Validation failed")
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .errors(errors)
                        .build()
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(HttpServletRequest request) {
        return ResponseEntity.status(404).body(
                ApiErrorResponse.builder()
                        .status(404)
                        .message("Not Found")
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodNotAllowed(HttpServletRequest request) {
        return ResponseEntity.status(405).body(
                ApiErrorResponse.builder()
                        .status(405)
                        .message("Method Not Allowed")
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleRSE(
            ResponseStatusException ex,
            HttpServletRequest request
    ) {

        int status = ex.getStatusCode().value();

        return ResponseEntity.status(status).body(
                ApiErrorResponse.builder()
                        .status(status)
                        .message(ex.getReason())
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(
            Exception ex,
            HttpServletRequest request
    ) {

        return ResponseEntity.status(500).body(
                ApiErrorResponse.builder()
                        .status(500)
                        .message(ex.getMessage())
                        .path(request.getRequestURI())
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }
}