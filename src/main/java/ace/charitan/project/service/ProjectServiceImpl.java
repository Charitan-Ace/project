package ace.charitan.project.service;

import ace.charitan.project.producer.KafkaMessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectServiceImpl {

    @Autowired
    private KafkaMessageProducer producer;

    public void testKafka(String message) {
        producer.sendMessage("test", message);
    }
}
