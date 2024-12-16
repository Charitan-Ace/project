package ace.charitan.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ace.charitan.project.controller.ProjectRequestBody.CreateProjectDto;
import ace.charitan.project.internal.InternalProjectDto;
import ace.charitan.project.internal.InternalProjectService;

@Service
class ProjectServiceImpl implements InternalProjectService, ExternalProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public InternalProjectDto createProject(CreateProjectDto createProjectDto) {

        // TODO: Change to based on auth
        Long charityId = 1L;

        Project project = new Project(createProjectDto, charityId);

        project = projectRepository.save(project);

        InternalProjectDto internalProjectDto = projectRepository.findOneById(project.getId()).get();

        return internalProjectDto;

    }

}
