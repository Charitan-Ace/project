package ace.charitan.project.internal.project.service;

import java.time.ZonedDateTime;
import java.util.List;

import ace.charitan.project.internal.project.controller.ProjectRequestBody.CreateProjectDto;
import ace.charitan.project.internal.project.controller.ProjectRequestBody.UpdateProjectDto;
import ace.charitan.project.internal.project.dto.project.InternalProjectDto;
import ace.charitan.project.internal.project.service.ProjectEnum.CategoryType;
import ace.charitan.project.internal.project.service.ProjectEnum.StatusType;
import ace.charitan.project.internal.utils.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class Project extends AbstractEntity implements InternalProjectDto {

    private String title;

    private String description;

    private Double goal;

    private ZonedDateTime startTime;

    private ZonedDateTime endTime;

    @Enumerated(EnumType.STRING)
    private StatusType statusType;

    @Enumerated(EnumType.STRING)
    private CategoryType categoryType;

    private Long charityId;

    private String countryIsoCode;

    // private List<Long> imageIdList;

    // private List<Long> videoIdList;

    // @Enumerated(EnumType.)

    // // Array of image url separated by comma
    // private String imageUrls;

    // private String thumbnailUrl ;

    // private String videoUrls;

    Project(CreateProjectDto createProjectDto, long charityId) {
        this.title = createProjectDto.getTitle();
        this.description = createProjectDto.getDescription();
        this.goal = createProjectDto.getGoal();
        this.startTime = createProjectDto.getStartTime();
        this.endTime = createProjectDto.getEndTime();
        this.statusType = StatusType.PENDING;
        this.categoryType = createProjectDto.getCategoryType();
        this.countryIsoCode = createProjectDto.getCountryIsoCode();
        this.charityId = charityId;
    }

    public void updateDetails(UpdateProjectDto updateProjectDto) {
        this.title = updateProjectDto.getTitle();
        this.description = updateProjectDto.getDescription();
        this.goal = updateProjectDto.getGoal();
        this.startTime = updateProjectDto.getStartTime();
        this.endTime = updateProjectDto.getEndTime();
        // this.categoryType = createProjectDto.getCategoryType();
        // this.countryIsoCode = createProjectDto.getCountryIsoCode();
    }

}
