package ace.charitan.project.consumer;


import ace.charitan.project.service.ProjectServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaMessageConsumer {

    @Autowired
    private ProjectServiceImpl service;

    @KafkaListener(topics = "test", groupId = "project")
    public void listen(String message) {
        System.out.println("Project microservice received message: " + message);
    }

}

