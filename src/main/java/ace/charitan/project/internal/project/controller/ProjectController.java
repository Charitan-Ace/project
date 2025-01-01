package ace.charitan.project.internal.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ace.charitan.project.internal.project.controller.ProjectRequestBody.CreateProjectDto;
import ace.charitan.project.internal.project.controller.ProjectRequestBody.SearchProjectsDto;
import ace.charitan.project.internal.project.controller.ProjectRequestBody.UpdateProjectDto;
import ace.charitan.project.internal.project.dto.project.InternalProjectDto;
import ace.charitan.project.internal.project.service.InternalProjectService;
import jakarta.validation.Valid;

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

    @PostMapping("/search")
    ResponseEntity<List<InternalProjectDto>> searchProjects(
            @RequestParam(value = "page", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
            @Validated @RequestBody SearchProjectsDto searchProjectsDto
    ) {
        List<InternalProjectDto> projectDtoList = internalProjectService.searchProjects(pageNo, pageSize,
                searchProjectsDto);
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{projectId}")
    ResponseEntity<InternalProjectDto> getProjectById(@PathVariable Long projectId) {

        InternalProjectDto projectDto = internalProjectService.getProjectById(projectId);
        return new ResponseEntity<>(projectDto, HttpStatus.OK);
    }

    @PutMapping("/update-details/{projectId}")
    ResponseEntity<InternalProjectDto> updateProjectDetails(@PathVariable Long projectId,
            @RequestBody UpdateProjectDto updateProjectDto) {
        InternalProjectDto projectDto = internalProjectService.updateProjectDetails(projectId, updateProjectDto);
        return new ResponseEntity<>(projectDto, HttpStatus.OK);
    }

    @PostMapping("/approve/{projectId}")
    ResponseEntity<InternalProjectDto> approveProject(@PathVariable Long projectId) {

        InternalProjectDto projectDto = internalProjectService.approveProject(projectId);
        return new ResponseEntity<>(projectDto, HttpStatus.OK);
    }

    @PostMapping("/halt/{projectId}")
    ResponseEntity<InternalProjectDto> haltProject(@PathVariable Long projectId) {

        InternalProjectDto projectDto = internalProjectService.haltProject(projectId);
        return new ResponseEntity<>(projectDto, HttpStatus.OK);
    }

    @PostMapping("/resume/{projectId}")
    ResponseEntity<InternalProjectDto> resumeProject(@PathVariable Long projectId) {

        InternalProjectDto projectDto = internalProjectService.resumeProject(projectId);
        return new ResponseEntity<>(projectDto, HttpStatus.OK);
    }

    @PostMapping("/delete/{projectId}")
    ResponseEntity<InternalProjectDto> deleteProject(@PathVariable Long projectId) {

        InternalProjectDto projectDto = internalProjectService.deleteProject(projectId);
        return new ResponseEntity<>(projectDto, HttpStatus.OK);
    }
}
