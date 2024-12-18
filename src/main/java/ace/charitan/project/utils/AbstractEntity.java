package ace.charitan.project.utils;

import java.io.Serializable;
import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public abstract class AbstractEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id; // MongoDB uses String for IDs by default

    @CreatedDate
    @Field("created_at") // Maps the field to `created_at` in MongoDB
    private Instant createdAt; // MongoDB stores dates as ISODate

    @LastModifiedDate
    @Field("updated_at") // Maps the field to `updated_at` in MongoDB
    private Instant updatedAt;

    public AbstractEntity(String id) {
        this.id = id;
    }
}
