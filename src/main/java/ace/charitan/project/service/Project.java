package ace.charitan.project.service;

import java.time.Instant;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import ace.charitan.project.service.ProjectEnum.CategoryType;
import ace.charitan.project.service.ProjectEnum.StatusType;
import ace.charitan.project.utils.AbstractEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "project") // MongoDB collection name
@Getter
@Setter
@NoArgsConstructor
public class Project extends AbstractEntity {

    /**
     * Project Details
     */

    private String title;

    private String description;

    private Double goal;

    private Instant startTime;

    private Instant endTime;

    private StatusType statusType; // Enum stored as String

    private CategoryType categoryType; // Enum stored as String

    private List<String> countryList;

    private List<String> regionList;

    private Long charityId;

    /*
     * Project media
     */

    // private List<String> imageUrls;

    // private String thumbnailUrl;

    // private List<String> videoUrls;

    // public Project(CreateProjectDto createProjectDto, long charityId) {
    //     this.setTitle(createProjectDto.getTitle());
    //     this.setDescription(createProjectDto.getDescription());
    //     this.setGoal(createProjectDto.getGoal());
    //     this.setStartTime(createProjectDto.getStartTime());
    //     this.setEndTime(createProjectDto.getEndTime());
    //     this.setStatusType(StatusType.PENDING);
    //     this.setCharityId(charityId);
    // }

    // public void updateDetails(UpdateProjectDto updateProjectDto) {
    //     this.setTitle(updateProjectDto.getTitle());
    //     this.setDescription(updateProjectDto.getDescription());
    //     this.setGoal(updateProjectDto.getGoal());
    //     this.setStartTime(updateProjectDto.getStartTime());
    //     this.setEndTime(updateProjectDto.getEndTime());
    // }
}