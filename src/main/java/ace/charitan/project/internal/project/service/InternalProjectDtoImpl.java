package ace.charitan.project.internal.project.service;

import java.time.ZonedDateTime;

import ace.charitan.project.internal.project.dto.project.InternalProjectDto;
import ace.charitan.project.internal.project.service.ProjectEnum.CategoryType;
import ace.charitan.project.internal.project.service.ProjectEnum.StatusType;
import lombok.Getter;

@Getter
public class InternalProjectDtoImpl implements InternalProjectDto {

    private String title;

    private String description;

    private Double goal;

    private ZonedDateTime startTime;

    private ZonedDateTime endTime;

    private StatusType statusType;

    private CategoryType categoryType;

    private String countryIsoCode;

    private String charityId;

    public InternalProjectDtoImpl(Project project) {
        this.title = project.getTitle();
        this.description = project.getDescription();
        this.goal = project.getGoal();
        this.startTime = project.getStartTime();
        this.endTime = project.getEndTime();
        this.statusType = project.getStatusType();
        this.categoryType = project.getCategoryType();
        this.countryIsoCode = project.getCountryIsoCode();
        this.charityId = project.getCharityId();
    }

}
