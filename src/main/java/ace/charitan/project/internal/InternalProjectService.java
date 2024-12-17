package ace.charitan.project.internal;

import ace.charitan.project.controller.ProjectRequestBody.CreateProjectDto;
import ace.charitan.project.controller.ProjectRequestBody.UpdateProjectDto;

public interface InternalProjectService {

    InternalProjectDto createProject(CreateProjectDto createProjectDto);

    InternalProjectDto getProjectById(Long projectId);

    InternalProjectDto updateProjectId(Long projectId, UpdateProjectDto updateProjectDto);

    InternalProjectDto approveProject(Long projectId);
}
