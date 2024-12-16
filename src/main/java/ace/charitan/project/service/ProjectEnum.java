package ace.charitan.project.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;

public class ProjectEnum {

    @AllArgsConstructor
    public static enum StatusType {
        PENDING("PENDING"), APPROVED("APPROVED"), ONGOING("ONGOING"), COMPLETED("COMPLETED"), ENDED("ENDED");

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

}
