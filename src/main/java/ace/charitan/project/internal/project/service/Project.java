package ace.charitan.project.internal.project.service;

import java.time.ZonedDateTime;
import java.util.List;

import ace.charitan.common.dto.media.ExternalMediaDto;
import ace.charitan.common.dto.project.ExternalProjectDto;
import ace.charitan.project.internal.common.AbstractEntity;
import ace.charitan.project.internal.project.controller.ProjectRequestBody.CreateProjectDto;
import ace.charitan.project.internal.project.controller.ProjectRequestBody.UpdateProjectDto;
import ace.charitan.project.internal.project.dto.project.InternalProjectDto;
import ace.charitan.project.internal.project.dto.project.InternalProjectDtoImpl;
import ace.charitan.project.internal.project.service.ProjectEnum.CategoryType;
import ace.charitan.project.internal.project.service.ProjectEnum.StatusType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "project")
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

    private String countryIsoCode;

    private String charityId;

    @Transient
    private List<ExternalMediaDto> mediaDtoList;

    // private List<Long> imageIdList;

    // private List<Long> videoIdList;

    // @Enumerated(EnumType.)

    // // Array of image url separated by comma
    // private String imageUrls;

    // private String thumbnailUrl ;

    // private String videoUrls;

    Project(CreateProjectDto createProjectDto, String charityId) {
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

    void updateDetails(UpdateProjectDto updateProjectDto) {
        // this.title = updateProjectDto.getTitle();
        // this.description = updateProjectDto.getDescription();
        // this.goal = updateProjectDto.getGoal();
        // this.startTime = updateProjectDto.getStartTime();
        // this.endTime = updateProjectDto.getEndTime();
        // this.categoryType = createProjectDto.getCategoryType();
        // this.countryIsoCode = createProjectDto.getCountryIsoCode();
    }

    ExternalProjectDto toExternalProjectDto() {
        return new ExternalProjectDto(getId().toString(), title, description, goal, startTime, endTime,
                statusType.toString(), categoryType.toString(), countryIsoCode, charityId);
    }

    InternalProjectDtoImpl toInternalProjectDtoImpl() {
        return new InternalProjectDtoImpl(
                getId().toString(), title, description, goal, startTime, endTime, statusType, categoryType,
                countryIsoCode, charityId, mediaDtoList);
    }

}
