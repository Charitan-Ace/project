package ace.charitan.project.internal.project.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
enum ProjectProducerTopic {
    PROJECT_GEOGRAPHY_GET_COUNTRY_BY_ISO_CODE("project-geography-get-country-by-iso-code");

    private String topic;

}
