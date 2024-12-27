// package ace.charitan.project.config;

// import java.util.HashMap;
// import java.util.Map;

// import org.apache.kafka.clients.consumer.ConsumerConfig;
// import org.apache.kafka.common.serialization.StringDeserializer;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.kafka.annotation.EnableKafka;
// import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
// import org.springframework.kafka.core.ConsumerFactory;
// import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

// @Configuration
// @EnableKafka
// class KafkaConsumerConfig {

//     @Value("${kafka.host-url}")
//     private String KAFKA_HOST_URL;

//     @Value("${kafka.group-id.notification}")
//     private String NOTIFICATION_GROUP_ID;

//     @Bean
//     public ConsumerFactory<String, String> defaultConsumerFactory() {

//         Map<String, Object> props = new HashMap<>();
//         props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_HOST_URL);
//         props.put(ConsumerConfig.GROUP_ID_CONFIG, NOTIFICATION_GROUP_ID);

//         props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//         props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

//         return new DefaultKafkaConsumerFactory<>(props);
//     }

//     @Bean
//     public ConcurrentKafkaListenerContainerFactory<String, String> defaultKafkaListenerContainerFactory() {
//         ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
//         factory.setConsumerFactory(defaultConsumerFactory());
//         return factory;
//     }
// }
