package ace.charitan.project.internal.service;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ace.charitan.project.internal.controller.ProjectRequestBody.CreateProjectDto;
import ace.charitan.project.internal.controller.ProjectRequestBody.UpdateProjectDto;
import ace.charitan.project.internal.dto.country.GetCountryByIsoCode.GetCountryByIsoCodeRequestDto;
import ace.charitan.project.internal.dto.project.InternalProjectDto;
import ace.charitan.project.internal.exception.ProjectException.InvalidProjectException;
import ace.charitan.project.internal.exception.ProjectException.NotFoundProjectException;
import ace.charitan.project.internal.service.ProjectEnum.StatusType;

@Service
class ProjectServiceImpl implements InternalProjectService {

    @Autowired
    private ProjectRepository projectRepository;

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

        // Check country existed or not
        GetCountryByIsoCodeRequestDto getCountryByIsoCodeRequestDto = new GetCountryByIsoCodeRequestDto(
                UUID.randomUUID().toString(), project.getCountryIsoCode());
        projectProducerService
                .send(getCountryByIsoCodeRequestDto);
        // Optional<CountryDto> optionalCountryDto =
        // getCountryDtoByCountryIsoCode(project.getCountryIsoCode());
        // if (optionalCountryDto.isEmpty()) {
        // throw new InvalidProjectException("Country code is invalid");
        // }

    }

    // private Optional<CountryDto> getCountryDtoByCountryIsoCode(String
    // countryIsoCode) {

    // // Check country iso code valid
    // String correlationId = UUID.randomUUID().toString();

    // CompletableFuture<CountryDto> asyncCallToGeographyServer = new
    // CompletableFuture<>();

    // pendingCountryRequests.put(correlationId, asyncCallToGeographyServer);

    // CountryCodeWithUuid countryIdWithUuidDto = new CountryCodeWithUuid(
    // correlationId, countryIsoCode);

    // try {

    // countryKafkaProducer.sendMessage(KafkaConstant.COUNTRY_TOPIC,
    // countryIdWithUuidDto);

    // CountryDto countryDto = asyncCallToGeographyServer.get(200,
    // TimeUnit.MILLISECONDS);

    // System.out.println(countryDto.getRegionName());

    // return Optional.of(countryDto);

    // } catch (InterruptedException | ExecutionException | TimeoutException e) {
    // System.err.println("Error in getting HabitatDescDto");
    // e.printStackTrace();
    // return Optional.empty();
    // }
    // }

    @Override
    public InternalProjectDto createProject(CreateProjectDto createProjectDto) {

        // TODO: Change to based on auth
        Long charityId = 1L;

        Project project = new Project(createProjectDto, charityId);
        validateProjectDetails(project);

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

}
