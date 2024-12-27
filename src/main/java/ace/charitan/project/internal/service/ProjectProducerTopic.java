package ace.charitan.project.internal.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
enum ProjectProducerTopic {
    PROJECT_GEOGRAPHY_GET_COUNTRY_BY_COUNTRY_CODE("PROJECT_GEOGRAPHY_GET_COUNTRY_BY_COUNTRY_CODE");

    private String topic;

}
