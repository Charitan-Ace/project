package ace.charitan.project.service;

import java.util.Date;

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
class Project extends AbstractEntity {

    private String title;

    private String description;

    private double goal;

    private Date startTime;

    private Date endTime;

    private StatusType statusType;

    // private CategoryType categoryType;

    // // Array of image url separated by comma
    // private String imageUrls;

    // private String thumbnailUrl ;

    // private String videoUrls;

}
