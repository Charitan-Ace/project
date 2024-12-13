package ace.charitan.project.controller;

import ace.charitan.project.dto.TestKafkaRequestDto;
import ace.charitan.project.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProjectController {

    @Autowired
    private ProjectService service;

    @GetMapping("/health")
    public String checkHealth() {
        return "Project service is oki";
    }

    @PostMapping("/test-kafka")
    public void testKafka(@RequestBody TestKafkaRequestDto dto) {
        service.testKafka(dto.getMessage());
    }
}
