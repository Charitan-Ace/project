package ace.charitan.project.internal.project.service;

import org.springframework.data.domain.Page;

import ace.charitan.project.internal.project.controller.ProjectRequestBody.CreateProjectDto;
import ace.charitan.project.internal.project.controller.ProjectRequestBody.SearchProjectsDto;
import ace.charitan.project.internal.project.controller.ProjectRequestBody.UpdateProjectDto;
import ace.charitan.project.internal.project.dto.project.InternalProjectDto;

public interface InternalProjectService {

    InternalProjectDto createProject(CreateProjectDto createProjectDto);

    Page<InternalProjectDto> searchProjects(Integer pageNo, Integer pageSize,
            SearchProjectsDto searchProjectsDto);

    InternalProjectDto getProjectById(Long projectId);

    InternalProjectDto updateProjectDetails(Long projectId, UpdateProjectDto updateProjectDto);

    InternalProjectDto approveProject(Long projectId);

    InternalProjectDto haltProject(Long projectId);

    InternalProjectDto resumeProject(Long projectId);

    InternalProjectDto deleteProject(Long projectId);

    InternalProjectDto completeProject(Long projectId);
}
