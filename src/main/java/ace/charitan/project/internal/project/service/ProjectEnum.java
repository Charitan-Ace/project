package ace.charitan.project.internal.project.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;

public class ProjectEnum {

    @AllArgsConstructor
    public static enum StatusType {
        PENDING("PENDING"),
        APPROVED("APPROVED"),
        HALTED("HALTED"),
        DELETED("DELETED");

        private String value;

        @JsonValue
        public String getValue() {
            return value;
        }

        @JsonCreator
        public static StatusType fromValue(String value) {
            for (StatusType status : values()) {
                String currentStatus = status.getValue();
                if (currentStatus.equals(value)) {
                    return status;
                }
            }

            // Return a response entity with a 400 Bad Request status
            throw new IllegalArgumentException("Invalid value for StatusType Enum: " + value);
        }
    }

    @AllArgsConstructor
    public static enum CategoryType {
        FOOD("FOOD"),
        HEALTH("HEALTH"),
        EDUCATION("EDUCATION"),
        ENVIRONMENT("ENVIRONMENT"),
        RELIGION("RELIGION"),
        HUMANITARIAN("HUMANITARIAN"),
        HOUSING("HOUSING"),
        OTHER("OTHER");

        private String value;

        @JsonValue
        public String getValue() {
            return value;
        }

        @JsonCreator
        public static CategoryType fromValue(String value) {
            for (CategoryType category : values()) {
                String currentCategory = category.getValue();
                if (currentCategory.equals(value)) {
                    return category;
                }
            }

            // Return a response entity with a 400 Bad Request category
            throw new IllegalArgumentException("Invalid value for CategoryType Enum: " + value);
        }

    }

}
