package ace.charitan.project.internal.project.dto.project;

import java.time.ZonedDateTime;

public interface InternalProjectDto {
    String getTitle();

    String getDescription();

    Double getGoal();

    ZonedDateTime getStartTime();

    ZonedDateTime getEndTime();

}
