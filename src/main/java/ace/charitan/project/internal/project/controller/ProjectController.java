package ace.charitan.project.internal.project.controller;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
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
            @RequestBody SearchProjectsDto searchProjectsDto) {
        Page<InternalProjectDto> page = internalProjectService.searchProjects(pageNo, pageSize,
                searchProjectsDto);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Total-Pages", String.valueOf(page.getTotalPages()));
        headers.setAccessControlExposeHeaders(Stream.of("Total-Pages").toList());
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/{projectId}")
    ResponseEntity<InternalProjectDto> getProjectById(@PathVariable String projectId) {
        System.out.println("vo request");
        InternalProjectDto projectDto = internalProjectService.getProjectById(projectId);
        return new ResponseEntity<>(projectDto, HttpStatus.OK);
    }

    @PutMapping("/update-details/{projectId}")
    ResponseEntity<InternalProjectDto> updateProjectDetails(@PathVariable String projectId,
            @RequestBody UpdateProjectDto updateProjectDto) {
        InternalProjectDto projectDto = internalProjectService.updateProjectDetails(projectId, updateProjectDto);
        return new ResponseEntity<>(projectDto, HttpStatus.OK);
    }

    @PostMapping("/approve/{projectId}")
    ResponseEntity<InternalProjectDto> approveProject(@PathVariable String projectId) {

        InternalProjectDto projectDto = internalProjectService.approveProject(projectId);
        return new ResponseEntity<>(projectDto, HttpStatus.OK);
    }

    @PostMapping("/halt/{projectId}")
    ResponseEntity<InternalProjectDto> haltProject(@PathVariable String projectId) {

        InternalProjectDto projectDto = internalProjectService.haltProject(projectId);
        return new ResponseEntity<>(projectDto, HttpStatus.OK);
    }

    @PostMapping("/resume/{projectId}")
    ResponseEntity<InternalProjectDto> resumeProject(@PathVariable String projectId) {

        InternalProjectDto projectDto = internalProjectService.resumeProject(projectId);
        return new ResponseEntity<>(projectDto, HttpStatus.OK);
    }

    @PostMapping("/delete/{projectId}")
    ResponseEntity<InternalProjectDto> deleteProject(@PathVariable String projectId) {

        InternalProjectDto projectDto = internalProjectService.deleteProject(projectId);
        return new ResponseEntity<>(projectDto, HttpStatus.OK);
    }

    @PostMapping("/complete/{projectId}")
    ResponseEntity<InternalProjectDto> completeProject(@PathVariable String projectId) {

        InternalProjectDto projectDto = internalProjectService.completeProject(projectId);
        return new ResponseEntity<>(projectDto, HttpStatus.OK);
    }

//    @PostMapping("/my")
//    ResponseEntity
}
