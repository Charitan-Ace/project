package ace.charitan.project.internal.service;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import ace.charitan.project.internal.dto.country.GetCountryByCountryCode.GetCountryByCountryCodeRequestDto;

class ProjectProducerService {

    @Autowired
    private KafkaTemplate<String, Serializable> kafkaTemplate;

    void send(ProjectProducerTopic topic, Serializable data) {

        kafkaTemplate.send(topic.getTopic(), data);

    }

    void send(GetCountryByCountryCodeRequestDto data) {
        send(ProjectProducerTopic.PROJECT_GEOGRAPHY_GET_COUNTRY_BY_COUNTRY_CODE, data);
    }

}
