package ace.charitan.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ace.charitan.project.controller.ProjectRequestBody.CreateProjectDto;
import ace.charitan.project.controller.ProjectRequestBody.UpdateProjectDto;
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

        return new ResponseEntity<InternalProjectDto>(projectDto, HttpStatus.CREATED);

    }

    @GetMapping("/{projectId}")
    ResponseEntity<InternalProjectDto> getProjectById(@PathVariable Long projectId) {

        InternalProjectDto projectDto = internalProjectService.getProjectById(projectId);
        return new ResponseEntity<>(projectDto, HttpStatus.OK);

    }

    @PutMapping("/{projectId}")
    ResponseEntity<InternalProjectDto> updateProject(@PathVariable Long projectId,
            @RequestBody UpdateProjectDto updateProjectDto) {
        InternalProjectDto projectDto = internalProjectService.updateProjectId(projectId, updateProjectDto);
        return new ResponseEntity<>(projectDto, HttpStatus.OK);
    }

    @PostMapping("/{projectId}")
    ResponseEntity<InternalProjectDto> approveProject(@PathVariable Long projectId) {
        return null;
    }
    

    

    // @PostMapping("/approve/{id}")
    // ResponseEntity<InternalProjectDto> approveProject(@PathVariable Long
    // projectId) {

    // InternalProjectDto projectDto =
    // internalProjectService.approveProject(projectId);
    // return ResponseEntity.ok(projectDto);
    // }

}
