package ace.charitan.project.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import ace.charitan.project.exception.ProjectException.InvalidProjectDateTime;

@ControllerAdvice
class ProjectExceptionHandler {
    @ExceptionHandler({ InvalidProjectDateTime.class })
    public ResponseEntity<String> handleException(
            RuntimeException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
