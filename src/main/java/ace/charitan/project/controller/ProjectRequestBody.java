package ace.charitan.project.controller;

import java.time.ZonedDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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

        @NotBlank(message = "Title must not be null or blank.")
        private String title;

        @NotBlank(message = "Description must not be null or blank.")
        private String description;

        @Min(value = 0, message = "Goal must be a non-negative number.")
        private double goal;

        @Future(message = "Start time must be in the future.")
        // @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}([+-]\\d{2}:\\d{2})$", message = "Start time must follow the format 'yyyy-MM-ddTHH:mm:ssXXX' (e.g., 2025-12-16T16:30:45+07:00).")
        private ZonedDateTime startTime;

        @Future(message = "End time must be in the future.")
        // @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}([+-]\\d{2}:\\d{2})$", message = "End time must follow the format 'yyyy-MM-ddTHH:mm:ssXXX' (e.g., 2025-12-16T16:30:45+07:00).")
        private ZonedDateTime endTime;
    }

}
