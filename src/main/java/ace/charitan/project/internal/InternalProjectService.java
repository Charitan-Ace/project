package ace.charitan.project.internal;

import ace.charitan.project.controller.ProjectRequestBody.CreateProjectDto;

public interface InternalProjectService {

    InternalProjectDto createProject(CreateProjectDto createProjectDto);

    // InternalProjectDto getProjectById(Long projectId);

    // InternalProjectDto updateProjectDetails(Long projectId, UpdateProjectDto updateProjectDto);

    // InternalProjectDto approveProject(Long projectId);

    // InternalProjectDto haltProject(Long projectId);

    // InternalProjectDto resumeProject(Long projectId);

    // InternalProjectDto deleteProject(Long projectId);
}
