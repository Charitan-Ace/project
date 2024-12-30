package ace.charitan.project.internal.project.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
class ProjectExceptionHandler {

    // For invalid request body
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {

        String message = "";
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            message += fieldError.getDefaultMessage();
        }

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", message);

        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }

    // For remaining custom exception handlers
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<Object> handleException(
            RuntimeException exception) {

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", exception.getMessage());

        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }
}
