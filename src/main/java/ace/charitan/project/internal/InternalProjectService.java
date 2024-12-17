package ace.charitan.project.internal;

import ace.charitan.project.controller.ProjectRequestBody.CreateProjectDto;

public interface InternalProjectService {

    InternalProjectDto createProject(CreateProjectDto createProjectDto);

    InternalProjectDto getProjectById(Long projectId);
}
