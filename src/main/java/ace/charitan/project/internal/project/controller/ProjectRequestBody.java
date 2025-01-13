package ace.charitan.project.internal.project.controller;

import ace.charitan.project.internal.project.service.ProjectEnum.StatusType;
import java.time.ZonedDateTime;

import ace.charitan.project.internal.project.service.ProjectEnum.CategoryType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
        private ZonedDateTime startTime;

        @Future(message = "End time must be in the future.")
        private ZonedDateTime endTime;

        @NotNull(message = "categoryType is invalid.")
        private CategoryType categoryType;

        @NotBlank(message = "Country ISO Code must not be null or blank.")
        private String countryIsoCode;

    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchProjectsDto {
        private String name;

        private StatusType status;

        private List<CategoryType> categoryTypes;

        private List<String> countryIsoCodes;

        private ZonedDateTime startTime;

        private ZonedDateTime endTime;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateProjectDto {

        @NotBlank(message = "Title must not be null or blank.")
        private String title;

        @NotBlank(message = "Description must not be null or blank.")
        private String description;

        @Min(value = 0, message = "Goal must be a non-negative number.")
        private double goal;

        /***
         * If old start time is passed the current date => cannot update
         */
        // @Future(message = "Start time must be in the future.")

        private ZonedDateTime startTime;

        // @Future(message = "End time must be in the future.")
        private ZonedDateTime endTime;

        @NotNull(message = "categoryType is invalid.")
        private CategoryType categoryType;

        @NotBlank(message = "Country ISO Code must not be null or blank.")
        private String countryIsoCode;
    }
}
