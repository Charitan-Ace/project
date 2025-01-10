package ace.charitan.project.internal.project.dto.project;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import ace.charitan.common.dto.media.ExternalMediaDto;
import ace.charitan.project.internal.project.service.ProjectEnum.CategoryType;
import ace.charitan.project.internal.project.service.ProjectEnum.StatusType;

public interface InternalProjectDto {

    String getTitle();

    String getDescription();

    Double getGoal();

    ZonedDateTime getStartTime();

    ZonedDateTime getEndTime();

    StatusType getStatusType();

    CategoryType getCategoryType();

    String getCountryIsoCode();

    String getCharityId();

    List<ExternalMediaDto> getMediaDtoList();

}
