package ace.charitan.project.internal.project.service;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.stereotype.Component;

import ace.charitan.common.dto.TestKafkaMessageDto;
import ace.charitan.common.dto.country.GetCountryByIsoCode.GetCountryByIsoCodeRequestDto;
import ace.charitan.common.dto.country.GetCountryByIsoCode.GetCountryByIsoCodeResponseDto;

@Component
class ProjectProducerService {

    // @Autowired
    // private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private ReplyingKafkaTemplate<String, Object, Object> replyingKafkaTemplate;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    private void send(ProjectProducerTopic topic, Serializable data) {
        kafkaTemplate.send(topic.getTopic(), data);
    }

    private Object sendAndReceive(
            ProjectProducerTopic topic, Serializable data) {
        try {

            ProducerRecord<String, Object> record = new ProducerRecord<>(topic.getTopic(), data);
            RequestReplyFuture<String, Object, Object> replyFuture = replyingKafkaTemplate.sendAndReceive(record);
            // SendResult<String, Object> sendResult = replyFuture.getSendFuture().get(10,
            // TimeUnit.SECONDS);
            ConsumerRecord<String, Object> consumerRecord = replyFuture.get(10, TimeUnit.SECONDS);
            return consumerRecord.value();
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    // GetCountryByIsoCodeResponseDto sendAndReceive(GetCountryByIsoCodeRequestDto
    // data) {
    // Object response =
    // sendAndReceive(ProjectProducerTopic.PROJECT_GEOGRAPHY_GET_COUNTRY_BY_ISO_CODE,
    // data);
    // return (GetCountryByIsoCodeResponseDto) response;
    // }

    TestKafkaMessageDto sendAndReceive(GetCountryByIsoCodeRequestDto data) {
        TestKafkaMessageDto response = (TestKafkaMessageDto) sendAndReceive(
                ProjectProducerTopic.PROJECT_GEOGRAPHY_GET_COUNTRY_BY_ISO_CODE, data);
        System.out.println(response.getName());
        return response;
    }

    // void send(GetCountryByIsoCodeRequestDto data) {
    // send(ProjectProducerTopic.PROJECT_GEOGRAPHY_GET_COUNTRY_BY_ISO_CODE, data);
    // }

}
