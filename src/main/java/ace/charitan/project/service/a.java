package ace.charitan.project.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;

public class a {
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
