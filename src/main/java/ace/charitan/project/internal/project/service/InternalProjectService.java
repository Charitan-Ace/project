package ace.charitan.project.internal.project.service;

import ace.charitan.project.internal.project.controller.ProjectRequestBody.CreateProjectDto;
import ace.charitan.project.internal.project.controller.ProjectRequestBody.UpdateProjectDto;
import ace.charitan.project.internal.project.dto.project.InternalProjectDto;

public interface InternalProjectService {

    InternalProjectDto createProject(CreateProjectDto createProjectDto);

    InternalProjectDto getProjectById(Long projectId);

    InternalProjectDto updateProjectDetails(Long projectId, UpdateProjectDto updateProjectDto);

    InternalProjectDto approveProject(Long projectId);

    InternalProjectDto haltProject(Long projectId);

    InternalProjectDto resumeProject(Long projectId);

    InternalProjectDto deleteProject(Long projectId);
}
