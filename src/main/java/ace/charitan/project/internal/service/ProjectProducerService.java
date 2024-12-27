package ace.charitan.project.internal.service;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import ace.charitan.project.internal.dto.country.GetCountryByIsoCode.GetCountryByIsoCodeRequestDto;

@Service
class ProjectProducerService {

    @Autowired
    private KafkaTemplate<String, Serializable> kafkaTemplate;

    void send(ProjectProducerTopic topic, Serializable data) {

        kafkaTemplate.send(topic.getTopic(), data);
    }

    void send(GetCountryByIsoCodeRequestDto data) {
        send(ProjectProducerTopic.PROJECT_GEOGRAPHY_GET_COUNTRY_BY_ISO_CODE, data);
    }

}
