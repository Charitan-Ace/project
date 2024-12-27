package ace.charitan.project.internal.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import ace.charitan.project.internal.dto.country.GetCountryByIsoCode.GetCountryByIsoCodeRequestDto;

@Service
class ProjectProducerService {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        // other configurations
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configs));
    }

    void send(ProjectProducerTopic topic, Serializable data) {

        kafkaTemplate.send(topic.getTopic(), data);
    }

    void send(GetCountryByIsoCodeRequestDto data) {
        send(ProjectProducerTopic.PROJECT_GEOGRAPHY_GET_COUNTRY_BY_ISO_CODE, data);
    }

}
