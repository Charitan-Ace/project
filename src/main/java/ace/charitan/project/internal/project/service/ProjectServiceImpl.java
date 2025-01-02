package ace.charitan.project.internal.project.service;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ace.charitan.project.internal.project.controller.ProjectRequestBody.CreateProjectDto;
import ace.charitan.project.internal.project.controller.ProjectRequestBody.SearchProjectsDto;
import ace.charitan.project.internal.project.controller.ProjectRequestBody.UpdateProjectDto;
import ace.charitan.project.internal.project.dto.project.InternalProjectDto;
import ace.charitan.project.internal.project.exception.ProjectException.InvalidProjectException;
import ace.charitan.project.internal.project.exception.ProjectException.NotFoundProjectException;
import ace.charitan.project.internal.project.service.ProjectEnum.StatusType;

@Service
class ProjectServiceImpl implements InternalProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectCustomRepository projectCustomRepository;

    @Autowired
    private ProjectProducerService projectProducerService;

    // Validate endTime - startTime >= 1 week
    private boolean validateStartEndTime(Project project) {

        Duration timeDifference = Duration.between(project.getStartTime(), project.getEndTime());
        return timeDifference.toDays() >= 7;

    }

    private void validateProjectDetails(Project project) {
        // Check start end time constraint
        if (!validateStartEndTime(project)) {
            throw new InvalidProjectException("Project must be last for at least 7 days");
        }

    }

    @Override
    @Transactional
    public InternalProjectDto createProject(CreateProjectDto createProjectDto) {

        // TODO: Change to based on auth
        String charityId = "fdsfdasfs-fasfdfdsfadsfs-fsdafadsfsad";

        Project project = new Project(createProjectDto, charityId);
        validateProjectDetails(project);

        return projectRepository.save(project);
        // return project.toInternalProjectDto();
        // return project.

    }

    @Override
    @Transactional(readOnly = true)
    public InternalProjectDto getProjectById(Long projectId) {
        Optional<Project> optionalProject = projectRepository.findById(projectId);

        if (optionalProject.isEmpty()) {
            throw new NotFoundProjectException();
        }

        return optionalProject.get();
    }

    @Override
    public InternalProjectDto updateProjectDetails(Long projectId, UpdateProjectDto updateProjectDto) {
        // If project not found
        Optional<Project> existedOptionalProject = projectRepository.findById(projectId);

        if (existedOptionalProject.isEmpty()) {
            throw new NotFoundProjectException();
        }

        Project project = existedOptionalProject.get();

        // Check date time is pass or not
        ZonedDateTime currentDateTime = ZonedDateTime.now();
        if (currentDateTime.isAfter(project.getStartTime()) || currentDateTime.isAfter(project.getEndTime())) {
            throw new InvalidProjectException("Start time and end time must not before the current date time");
        }

        project.updateDetails(updateProjectDto);

        if (!validateStartEndTime(project)) {
            throw new InvalidProjectException("Project must be last for at least 7 days");
        }

        project = projectRepository.save(project);

        return project;
    }

    @Override
    public InternalProjectDto approveProject(Long projectId) {
        // If project not found
        Optional<Project> existedOptionalProject = projectRepository.findById(projectId);

        if (existedOptionalProject.isEmpty()) {
            throw new NotFoundProjectException();
        }

        Project project = existedOptionalProject.get();

        // If project status is not PENDING
        if (!project.getStatusType().equals(StatusType.PENDING)) {
            throw new InvalidProjectException("Project can be approved if status is PENDING");
        }

        // If project is approved after endTime
        ZonedDateTime currentDateTime = ZonedDateTime.now();
        if (currentDateTime.isAfter(project.getEndTime())) {
            throw new InvalidProjectException(
                    "Project can not be approved if end time is before the current date time");
        }

        project.setStatusType(StatusType.APPROVED);
        project = projectRepository.save(project);

        return project;
    }

    @Override
    public InternalProjectDto haltProject(Long projectId) {
        // If project not found
        Optional<Project> existedOptionalProject = projectRepository.findById(projectId);

        if (existedOptionalProject.isEmpty()) {
            throw new NotFoundProjectException();
        }

        Project project = existedOptionalProject.get();

        // If project status is not APPROVED
        if (!project.getStatusType().equals(StatusType.APPROVED)) {
            throw new InvalidProjectException("Project can be halted if status is APPROVED");
        }

        // If project is halted after endTime
        ZonedDateTime currentDateTime = ZonedDateTime.now();
        if (currentDateTime.isAfter(project.getEndTime())) {
            throw new InvalidProjectException(
                    "Project can not be halted if end time is before the current date time");
        }

        project.setStatusType(StatusType.HALTED);
        project = projectRepository.save(project);

        return project;
    }

    @Override
    public InternalProjectDto resumeProject(Long projectId) {
        // If project not found
        Optional<Project> existedOptionalProject = projectRepository.findById(projectId);

        if (existedOptionalProject.isEmpty()) {
            throw new NotFoundProjectException();
        }

        Project project = existedOptionalProject.get();

        // If project status is not HALTED
        if (!project.getStatusType().equals(StatusType.HALTED)) {
            throw new InvalidProjectException("Project can be resumed if status is HALTED");
        }

        // If project is resumed after endTime
        ZonedDateTime currentDateTime = ZonedDateTime.now();
        if (currentDateTime.isAfter(project.getEndTime())) {
            throw new InvalidProjectException(
                    "Project can not be resumed if end time is before the current date time");
        }

        project.setStatusType(StatusType.APPROVED);
        project = projectRepository.save(project);

        return project;
    }

    @Override
    public InternalProjectDto deleteProject(Long projectId) {
        // If project not found
        Optional<Project> existedOptionalProject = projectRepository.findById(projectId);

        if (existedOptionalProject.isEmpty()) {
            throw new NotFoundProjectException();
        }

        Project project = existedOptionalProject.get();

        // If project status is not HALTED
        if (!project.getStatusType().equals(StatusType.DELETED)) {
            throw new InvalidProjectException("Project can be deleted if status is RESUMED");
        }

        project.setStatusType(StatusType.DELETED);
        project = projectRepository.save(project);

        return project;
    }

    @Override
    public Page<InternalProjectDto> searchProjects(Integer pageNo, Integer pageSize,
            SearchProjectsDto searchProjectsDto) {

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

        // Get pageable result1
        // return projectCustomRepository.searchProjects(searchProjectsDto, pageable);

        return projectRepository.findByCountryIsoCode(searchProjectsDto.getCountryIsoCode(), pageable);
    }

}
