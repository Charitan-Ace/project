package ace.charitan.project.controller;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ProjectRequestBody {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateProjectDto {

        private String title;

        private String description;

        private double goal;

        private Date startTime;

        private Date endTime;

    }

}
