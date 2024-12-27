package ace.charitan.project.internal.service;

import ace.charitan.project.internal.controller.ProjectRequestBody.CreateProjectDto;
import ace.charitan.project.internal.controller.ProjectRequestBody.UpdateProjectDto;
import ace.charitan.project.internal.dto.project.InternalProjectDto;

public interface InternalProjectService {

    InternalProjectDto createProject(CreateProjectDto createProjectDto);

    InternalProjectDto getProjectById(Long projectId);

    InternalProjectDto updateProjectDetails(Long projectId, UpdateProjectDto updateProjectDto);

    InternalProjectDto approveProject(Long projectId);

    InternalProjectDto haltProject(Long projectId);

    InternalProjectDto resumeProject(Long projectId);

    InternalProjectDto deleteProject(Long projectId);
}
