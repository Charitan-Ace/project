package ace.charitan.project.controller;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ace.charitan.project.utils.Constants;

public class ProjectRequestBody {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateProjectDto {

        private String title;

        private String description;

        private double goal;

        @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT)
        @Pattern(regexp = Constants.DATE_TIME_REGEX, message = Constants.INVALID_DATE_TIME_MESSAGE)
        @Future
        private Date startTime;

        @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT)
        @Pattern(regexp = Constants.DATE_TIME_REGEX, message = Constants.INVALID_DATE_TIME_MESSAGE)
        @Future
        private Date endTime;

    }

}
