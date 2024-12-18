package ace.charitan.project.service;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ace.charitan.project.controller.ProjectRequestBody.CreateProjectDto;
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
        // Long charityId = 1L;

        // Project project = new Project(createProjectDto, charityId);

        // if (!validateStartEndTime(project)) {
        // throw new InvalidProjectException("Project must be last for at least 7
        // days");
        // }

        // return projectRepository.save(project);

        return null;

    }

    // @Override
    // public InternalProjectDto getProjectById(Long projectId) {
    // Optional<Project> optionalProject = projectRepository.findById(projectId);

    // if (optionalProject.isEmpty()) {
    // throw new NotFoundProjectException();
    // }

    // return optionalProject.get();
    // }

    // @Override
    // public InternalProjectDto updateProjectDetails(Long projectId,
    // UpdateProjectDto updateProjectDto) {
    // // If project not found
    // Optional<Project> existedOptionalProject =
    // projectRepository.findById(projectId);

    // if (existedOptionalProject.isEmpty()) {
    // throw new NotFoundProjectException();
    // }

    // Project project = existedOptionalProject.get();

    // // Check date time is pass or not
    // ZonedDateTime currentDateTime = ZonedDateTime.now();
    // if (currentDateTime.isAfter(project.getStartTime()) ||
    // currentDateTime.isAfter(project.getEndTime())) {
    // throw new InvalidProjectException("Start time and end time must not before
    // the current date time");
    // }

    // project.updateDetails(updateProjectDto);

    // if (!validateStartEndTime(project)) {
    // throw new InvalidProjectException("Project must be last for at least 7
    // days");
    // }

    // project = projectRepository.save(project);

    // return project;
    // }

    // @Override
    // public InternalProjectDto approveProject(Long projectId) {
    // // If project not found
    // Optional<Project> existedOptionalProject =
    // projectRepository.findById(projectId);

    // if (existedOptionalProject.isEmpty()) {
    // throw new NotFoundProjectException();
    // }

    // Project project = existedOptionalProject.get();

    // // If project status is not PENDING
    // if (!project.getStatusType().equals(StatusType.PENDING)) {
    // throw new InvalidProjectException("Project can be approved if status is
    // PENDING");
    // }

    // // If project is approved after endTime
    // ZonedDateTime currentDateTime = ZonedDateTime.now();
    // if (currentDateTime.isAfter(project.getEndTime())) {
    // throw new InvalidProjectException(
    // "Project can not be approved if end time is before the current date time");
    // }

    // project.setStatusType(StatusType.APPROVED);
    // project = projectRepository.save(project);

    // return project;
    // }

    // @Override
    // public InternalProjectDto haltProject(Long projectId) {
    // // If project not found
    // Optional<Project> existedOptionalProject =
    // projectRepository.findById(projectId);

    // if (existedOptionalProject.isEmpty()) {
    // throw new NotFoundProjectException();
    // }

    // Project project = existedOptionalProject.get();

    // // If project status is not APPROVED
    // if (!project.getStatusType().equals(StatusType.APPROVED)) {
    // throw new InvalidProjectException("Project can be halted if status is
    // APPROVED");
    // }

    // // If project is halted after endTime
    // ZonedDateTime currentDateTime = ZonedDateTime.now();
    // if (currentDateTime.isAfter(project.getEndTime())) {
    // throw new InvalidProjectException(
    // "Project can not be halted if end time is before the current date time");
    // }

    // project.setStatusType(StatusType.HALTED);
    // project = projectRepository.save(project);

    // return project;
    // }

    // @Override
    // public InternalProjectDto resumeProject(Long projectId) {
    // // If project not found
    // Optional<Project> existedOptionalProject =
    // projectRepository.findById(projectId);

    // if (existedOptionalProject.isEmpty()) {
    // throw new NotFoundProjectException();
    // }

    // Project project = existedOptionalProject.get();

    // // If project status is not HALTED
    // if (!project.getStatusType().equals(StatusType.HALTED)) {
    // throw new InvalidProjectException("Project can be resumed if status is
    // HALTED");
    // }

    // // If project is resumed after endTime
    // ZonedDateTime currentDateTime = ZonedDateTime.now();
    // if (currentDateTime.isAfter(project.getEndTime())) {
    // throw new InvalidProjectException(
    // "Project can not be resumed if end time is before the current date time");
    // }

    // project.setStatusType(StatusType.APPROVED);
    // project = projectRepository.save(project);

    // return project;
    // }

    // @Override
    // public InternalProjectDto deleteProject(Long projectId) {
    // // If project not found
    // Optional<Project> existedOptionalProject =
    // projectRepository.findById(projectId);

    // if (existedOptionalProject.isEmpty()) {
    // throw new NotFoundProjectException();
    // }

    // Project project = existedOptionalProject.get();

    // // If project status is not HALTED
    // if (!project.getStatusType().equals(StatusType.DELETED)) {
    // throw new InvalidProjectException("Project can be deleted if status is
    // RESUMED");
    // }

    // project.setStatusType(StatusType.DELETED);
    // project = projectRepository.save(project);

    // return project;
    // }

}
