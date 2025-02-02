package ace.charitan.project.internal.project.service;

import ace.charitan.common.dto.TestKafkaMessageDto;
import ace.charitan.common.dto.donation.DonationDto;
import ace.charitan.common.dto.donation.DonationsDto;
import ace.charitan.common.dto.donation.GetDonationsByProjectIdDto;
import ace.charitan.common.dto.donation.GetProjectIdsByDonorIdRequestDto;
import ace.charitan.common.dto.donation.GetProjectIdsByDonorIdResponseDto;
import ace.charitan.common.dto.media.GetMediaByProjectIdRequestDto;
import ace.charitan.common.dto.media.GetMediaByProjectIdResponseDto;
import ace.charitan.common.dto.subscription.NewProjectSubscriptionDto.NewProjectSubscriptionRequestDto;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.stereotype.Component;

@Component
class ProjectProducerService {

  // @Autowired
  // private KafkaTemplate<String, Object> kafkaTemplate;

  @Autowired
  private ReplyingKafkaTemplate<String, Object, Object> replyingKafkaTemplate;

  @Autowired
  private KafkaTemplate<String, Object> kafkaTemplate;

  private void send(ProjectProducerTopic topic, Serializable data) {
    try {
      kafkaTemplate.send(topic.getTopic(), data);
    } catch (Exception e) {
      // TODO: handle exception
    }
  }

  private Object sendAndReceive(ProjectProducerTopic topic, Serializable data) {
    try {

      ProducerRecord<String, Object> record = new ProducerRecord<>(topic.getTopic(), data);
      RequestReplyFuture<String, Object, Object> replyFuture = replyingKafkaTemplate.sendAndReceive(record);
      // SendResult<String, Object> sendResult = replyFuture.getSendFuture().get(10,
      // TimeUnit.SECONDS);
      ConsumerRecord<String, Object> consumerRecord = replyFuture.get(20, TimeUnit.SECONDS);
      return consumerRecord.value();
    } catch (Exception exception) {
      exception.printStackTrace();
      return null;
    }
  }

  TestKafkaMessageDto sendAndReceive(TestKafkaMessageDto data) {
    System.out.println("start to send");
    TestKafkaMessageDto response = (TestKafkaMessageDto) sendAndReceive(
        ProjectProducerTopic.PROJECT_GEOGRAPHY_GET_COUNTRY_BY_ISO_CODE, data);
    System.out.println("Project receive" + response.getName());
    return response;
  }

  GetMediaByProjectIdResponseDto sendAndReceive(GetMediaByProjectIdRequestDto data) {
    GetMediaByProjectIdResponseDto response = (GetMediaByProjectIdResponseDto) sendAndReceive(
        ProjectProducerTopic.PROJECT_MEDIA_GET_MEDIA_BY_PROJECT_ID, data);
    System.out.println(response);
    return response;
  }

  DonationsDto sendAndReceive(GetDonationsByProjectIdDto data) {
    DonationsDto res = (DonationsDto) sendAndReceive(ProjectProducerTopic.PROJECT_DONATION_GET_DONATION_BY_PROJECT_ID,
        data.id());
    System.out.println(res);
    return res;
  }

  GetProjectIdsByDonorIdResponseDto sendAndReceive(GetProjectIdsByDonorIdRequestDto dto) {
    GetProjectIdsByDonorIdResponseDto res = (GetProjectIdsByDonorIdResponseDto) sendAndReceive(
        ProjectProducerTopic.PROJECT_DONATION_GET_PROJECTS_BY_DONOR_ID,
        dto);
    System.out.println(res);
    return res;

  }

  void send(NewProjectSubscriptionRequestDto data) {
    send(ProjectProducerTopic.PROJECT_SUBSCRIPTION_NEW_PROJECT, data);
  }

}
