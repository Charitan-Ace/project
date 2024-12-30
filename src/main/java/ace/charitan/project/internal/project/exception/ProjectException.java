package ace.charitan.project.internal.project.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ProjectException {

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public static class InvalidProjectException extends RuntimeException {

        public InvalidProjectException(String message) {
            super(message);
        }

    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public static class NotFoundProjectException extends RuntimeException {

        public NotFoundProjectException() {
            super("Not found project");
        }

    }

}
