package ace.charitan.project.controller;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ace.charitan.project.controller.ProjectRequestBody.CreateProjectDto;
import ace.charitan.project.internal.InternalProjectDto;
import ace.charitan.project.internal.InternalProjectService;

@RestController
class ProjectController {

    @Autowired
    private InternalProjectService internalProjectService;

    @GetMapping("/health")
    String checkHealth() {
        return "Project service is oki";
    }

    @PostMapping("/create")
    ResponseEntity<InternalProjectDto> createProject(@Validated @RequestBody CreateProjectDto createProjectDto) {

        InternalProjectDto projectDto = internalProjectService.createProject(createProjectDto);
        System.out.println(projectDto.getTitle());

        if (Objects.isNull(projectDto)) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(projectDto);
    }

}
