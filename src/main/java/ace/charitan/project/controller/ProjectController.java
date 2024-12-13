package ace.charitan.project.controller;

import ace.charitan.project.service.ProjectServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ProjectController {

    @Autowired
    private ProjectServiceImpl service;

    @GetMapping("/health")
    String checkHealth() {
        return "Project service is oki";
    }

//    @PostMapping("/test-kafka")
//    void testKafka(@RequestBody TestKafkaRequestDto dto) {
//        service.testKafka(dto.getMessage());
//    }
}
