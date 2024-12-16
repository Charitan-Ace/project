package ace.charitan.project.controller;

import java.time.ZonedDateTime;

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

        // @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT)
        @Future
        private ZonedDateTime startTime;

        // @DateTimeFormat(pattern = Constants.DATE_TIME_FORMAT)
        @Future
        private ZonedDateTime endTime;

    }

}
