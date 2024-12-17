package ace.charitan.project.service;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ace.charitan.project.controller.ProjectRequestBody.CreateProjectDto;
import ace.charitan.project.exception.ProjectException.InvalidProjectDateTime;
import ace.charitan.project.internal.InternalProjectDto;
import ace.charitan.project.internal.InternalProjectService;

@Service
class ProjectServiceImpl implements InternalProjectService, ExternalProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public InternalProjectDto createProject(CreateProjectDto createProjectDto) {
        // Validate endTime - startTime >= 1 week
        Duration timeDifference = Duration.between(createProjectDto.getStartTime(), createProjectDto.getEndTime());
        if (timeDifference.toDays() < 7) {
            throw new InvalidProjectDateTime("Project must be last for at least 7 days");
        }

        // TODO: Change to based on auth
        Long charityId = 1L;

        Project project = new Project(createProjectDto, charityId);

        return projectRepository.save(project);

    }

}
