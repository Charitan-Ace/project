package ace.charitan.project.service;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ace.charitan.project.controller.ProjectRequestBody.CreateProjectDto;
import ace.charitan.project.controller.ProjectRequestBody.UpdateProjectDto;
import ace.charitan.project.exception.ProjectException.InvalidProjectDateTimeException;
import ace.charitan.project.exception.ProjectException.NotFoundProjectException;
import ace.charitan.project.internal.InternalProjectDto;
import ace.charitan.project.internal.InternalProjectService;

@Service
class ProjectServiceImpl implements InternalProjectService, ExternalProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    // Validate endTime - startTime >= 1 week
    private boolean validateStartEndTime(Project project) {

        Duration timeDifference = Duration.between(project.getStartTime(), project.getEndTime());
        return timeDifference.toDays() >= 7;

    }

    @Override
    public InternalProjectDto createProject(CreateProjectDto createProjectDto) {

        // TODO: Change to based on auth
        Long charityId = 1L;

        Project project = new Project(createProjectDto, charityId);

        if (!validateStartEndTime(project)) {
            throw new InvalidProjectDateTimeException("Project must be last for at least 7 days");
        }

        return projectRepository.save(project);

    }

    @Override
    public InternalProjectDto getProjectById(Long projectId) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);

        if (optionalProject.isEmpty()) {
            throw new NotFoundProjectException();
        }

        return optionalProject.get();
    }

    @Override
    public InternalProjectDto updateProjectId(Long projectId, UpdateProjectDto updateProjectDto) {
        // If project not found
        Optional<Project> existedOptionalProject = projectRepository.findById(projectId);

        if (existedOptionalProject.isEmpty()) {
            throw new NotFoundProjectException();
        }

        Project project = existedOptionalProject.get();

        // Check date time is pass or not
        ZonedDateTime currentDateTime = ZonedDateTime.now();
        if (currentDateTime.isAfter(project.getStartTime()) || currentDateTime.isAfter(project.getEndTime())) {
            throw new InvalidProjectDateTimeException("Start time and end time must not before the current date time");
        }

        project.updateDetails(updateProjectDto);

        if (!validateStartEndTime(project)) {
            throw new InvalidProjectDateTimeException("Project must be last for at least 7 days");
        }

        project = projectRepository.save(project);

        return project;
    }

}
