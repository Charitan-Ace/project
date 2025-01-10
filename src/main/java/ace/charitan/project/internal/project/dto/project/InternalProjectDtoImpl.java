package ace.charitan.project.internal.project.dto.project;

import java.time.ZonedDateTime;
import java.util.List;

import ace.charitan.common.dto.media.ExternalMediaDto;
import ace.charitan.project.internal.project.service.ProjectEnum.CategoryType;
import ace.charitan.project.internal.project.service.ProjectEnum.StatusType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InternalProjectDtoImpl implements InternalProjectDto {

    private String id;

    private String title;

    private String description;

    private Double goal;

    private ZonedDateTime startTime;

    private ZonedDateTime endTime;

    private StatusType statusType;

    private CategoryType categoryType;

    private String countryIsoCode;

    private String charityId;

    private List<ExternalMediaDto> mediaDtoList;

}
