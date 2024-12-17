package ace.charitan.project.service;

import java.time.ZonedDateTime;

import ace.charitan.project.controller.ProjectRequestBody.CreateProjectDto;
import ace.charitan.project.controller.ProjectRequestBody.UpdateProjectDto;
import ace.charitan.project.internal.InternalProjectDto;
import ace.charitan.project.service.ProjectEnum.StatusType;
import ace.charitan.project.utils.AbstractEntity;
import jakarta.persistence.Entity;
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

    private StatusType statusType;

    private Long charityId;

    // private CategoryType categoryType;

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
        this.charityId = charityId;
    }

    public void update(UpdateProjectDto updateProjectDto) {
        this.title = updateProjectDto.getTitle();
        this.description = updateProjectDto.getDescription();
        this.goal = updateProjectDto.getGoal();
        this.startTime = updateProjectDto.getStartTime();
        this.endTime = updateProjectDto.getEndTime();
    }

}
