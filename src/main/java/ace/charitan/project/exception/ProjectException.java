package ace.charitan.project.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ProjectException {

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public static class InvalidProjectDateTime extends RuntimeException {

        public InvalidProjectDateTime(String message) {
            super(message);
        }

    }

}
