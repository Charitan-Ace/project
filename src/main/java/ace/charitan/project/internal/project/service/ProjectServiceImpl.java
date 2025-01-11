package ace.charitan.project.internal.project.service;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import ace.charitan.common.dto.media.ExternalMediaDto;
import ace.charitan.common.dto.media.GetMediaByProjectIdRequestDto;
import ace.charitan.common.dto.media.GetMediaByProjectIdResponseDto;
import ace.charitan.common.dto.subscription.NewProjectSubscriptionDto.NewProjectSubscriptionRequestDto;
import ace.charitan.project.internal.project.controller.ProjectRequestBody.CreateProjectDto;
import ace.charitan.project.internal.project.controller.ProjectRequestBody.SearchProjectsDto;
import ace.charitan.project.internal.project.controller.ProjectRequestBody.UpdateProjectDto;
import ace.charitan.project.internal.project.dto.project.InternalProjectDto;
import ace.charitan.project.internal.project.exception.ProjectException.InvalidProjectException;
import ace.charitan.project.internal.project.exception.ProjectException.NotFoundProjectException;
import ace.charitan.project.internal.project.service.ProjectEnum.StatusType;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Service
class ProjectServiceImpl implements InternalProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private EntityManager entityManager;

    // @Autowired
    // private ProjectCustomRepository projectCustomRepository;

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

    private void addMediaListToProject(Project project, List<ExternalMediaDto> mediaDtoList) {
        project.setMediaDtoList(mediaDtoList);
    }

    @Override
    @Transactional
    public InternalProjectDto createProject(CreateProjectDto createProjectDto) {

        // TODO: Change to based on auth
        String charityId = "fdsfdasfs-fasfdfdsfadsfs-fsdafadsfsad";

        Project project = new Project(createProjectDto, charityId);
        validateProjectDetails(project);

        project = projectRepository.save(project);

        // Send topic to subscription service
        projectProducerService.send(new NewProjectSubscriptionRequestDto(project.toExternalProjectDto()));

        // return project.toInternalProjectDto();
        return project;

    }

    @Override
    @Transactional
    public InternalProjectDto getProjectById(String projectId) {
        Optional<Project> optionalProject = projectRepository.findById(UUID.fromString(projectId));

        if (optionalProject.isEmpty()) {
            throw new NotFoundProjectException();
        }

        Project projectDto = optionalProject.get();

        // TODO: Add videos and images query
        List<String> projectIdList = Arrays.asList(projectDto.getId().toString());
        GetMediaByProjectIdResponseDto getMediaByProjectIdResponseDto = projectProducerService
                .sendAndReceive(new GetMediaByProjectIdRequestDto(projectIdList));

        // Add media to project
        addMediaListToProject(projectDto,
                getMediaByProjectIdResponseDto.getMediaListDtoList().get(0).getMediaDtoList());

        // Convert to impl
        return projectDto.toInternalProjectDtoImpl();
    }

    @Override
    public Page<InternalProjectDto> searchProjects(Integer pageNo, Integer pageSize,
            SearchProjectsDto searchProjectsDto) {

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

        // Get pageable result1
        // return projectCustomRepository.searchProjects(searchProjectsDto, pageable);

        return projectRepository.findByCountryIsoCode(searchProjectsDto.getCountryIsoCode(), pageable);
    }

    @Override
    @Transactional
    public InternalProjectDto updateProjectDetails(String projectId, UpdateProjectDto updateProjectDto) {
        // If project not found
        Optional<Project> existedOptionalProject = projectRepository.findById(UUID.fromString(projectId));

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
    @Transactional
    public InternalProjectDto approveProject(String projectId) {
        // If project not found
        Optional<Project> existedOptionalProject = projectRepository.findById(UUID.fromString(projectId));

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
    @Transactional
    public InternalProjectDto haltProject(String projectId) {
        // If project not found
        Optional<Project> existedOptionalProject = projectRepository.findById(UUID.fromString(projectId));

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
    @Transactional
    public InternalProjectDto resumeProject(String projectId) {
        // If project not found
        Optional<Project> existedOptionalProject = projectRepository.findById(UUID.fromString(projectId));

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
    @Transactional
    public InternalProjectDto deleteProject(String projectId) {
        try {

            // Set shard to PROJECT and retrieve the entity
            ShardContextHolder.setCurrentShard(ShardContextConstant.PROJECT);
            Optional<Project> existedOptionalProject = projectRepository.findById(UUID.fromString(projectId));

            if (existedOptionalProject.isEmpty()) {
                throw new NotFoundProjectException();
            }

            Project project = existedOptionalProject.get();

            // Ensure project status is HALTED before deletion
            if (!project.getStatusType().equals(StatusType.HALTED)) {
                throw new InvalidProjectException("Project can be deleted if status is HALTED");
            }

            // Set status to DELETED
            project.setStatusType(StatusType.DELETED);

            // Detach the project entity
            entityManager.detach(project);

            // Delete the project from the PROJECT shard
            projectRepository.deleteById(project.getId());

            // Clear the persistence context to avoid stale entity issues
            entityManager.flush();
            entityManager.clear();

            // Switch to PROJECT_DELETED shard
            ShardContextHolder.setCurrentShard(ShardContextConstant.PROJECT_DELETED);

            // Create a new instance of the deleted project and save in PROJECT_DELETED
            // shard
            Project deletedProject = new Project(project);
            deletedProject = projectRepository.save(deletedProject);

            // Reset to the default shard (PROJECT)
            ShardContextHolder.setCurrentShard(ShardContextConstant.PROJECT);

            return deletedProject;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    @Transactional
    public InternalProjectDto completeProject(String projectId) {
        // If project not found
        Optional<Project> existedOptionalProject = projectRepository.findById(UUID.fromString(projectId));

        if (existedOptionalProject.isEmpty()) {
            throw new NotFoundProjectException();
        }

        Project project = existedOptionalProject.get();

        // If project status is not HALTED
        if (!project.getStatusType().equals(StatusType.APPROVED)) {
            throw new InvalidProjectException("Project can be completed if status is APPROVED");
        }

        project.setStatusType(StatusType.COMPLETED);
        project = projectRepository.save(project);

        return project;
    }

}
