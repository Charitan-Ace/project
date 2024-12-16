package ace.charitan.project.internal;

import java.util.Date;

public interface InternalProjectDto {
    String getTitle();

    String getDescription();

    Double getGoal();

    Date getStartTime();

    Date getEndTime();

}
