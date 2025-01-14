package ace.charitan.project.internal.project.service;

import ace.charitan.common.dto.donation.DonationDto;
import ace.charitan.common.dto.donation.DonationsDto;
import ace.charitan.common.dto.donation.GetDonationsByProjectIdDto;
import ace.charitan.common.dto.media.ExternalMediaDto;
import ace.charitan.common.dto.media.GetMediaByProjectIdRequestDto;
import ace.charitan.common.dto.media.GetMediaByProjectIdResponseDto;
import ace.charitan.common.dto.project.ExternalProjectDto;
import ace.charitan.common.dto.project.GetProjectByCharityIdDto.GetProjectByCharityIdRequestDto;
import ace.charitan.common.dto.project.GetProjectByCharityIdDto.GetProjectByCharityIdResponseDto;
import ace.charitan.common.dto.project.UpdateProjectMediaDto;
import ace.charitan.common.dto.subscription.NewProjectSubscriptionDto.NewProjectSubscriptionRequestDto;
import ace.charitan.project.internal.auth.AuthModel;
import ace.charitan.project.internal.auth.AuthUtils;
import ace.charitan.project.internal.project.controller.ProjectRequestBody.CreateProjectDto;
import ace.charitan.project.internal.project.controller.ProjectRequestBody.SearchProjectsDto;
import ace.charitan.project.internal.project.controller.ProjectRequestBody.UpdateProjectDto;
import ace.charitan.project.internal.project.dto.project.InternalProjectDto;
import ace.charitan.project.internal.project.dto.project.InternalProjectDtoImpl;
import ace.charitan.project.internal.project.exception.ProjectException.InvalidProjectException;
import ace.charitan.project.internal.project.exception.ProjectException.NotFoundProjectException;
import ace.charitan.project.internal.project.service.ProjectEnum.StatusType;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
class ProjectServiceImpl implements InternalProjectService {

  @Value("${data.charitan-id}")
  private String DEFAULT_CHARITAN_ID;

  @Autowired private ProjectRepository projectRepository;

  @Autowired private ProjectShardService projectShardService;

  @Autowired private ProjectProducerService projectProducerService;

  @Autowired private ProjectRedisService projectRedisService;

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

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

  private void addMediaListToProject(
      InternalProjectDtoImpl project, List<ExternalMediaDto> mediaDtoList) {
    project.setMediaDtoList(mediaDtoList);
  }

  @Override
  @Transactional
  public InternalProjectDto createProject(CreateProjectDto createProjectDto) {

    AuthModel authModel = AuthUtils.getUserDetails();

    String charityId = !Objects.isNull(authModel) ? authModel.getUsername() : DEFAULT_CHARITAN_ID;

    Project project = new Project(createProjectDto, charityId);
    validateProjectDetails(project);

    project = projectRepository.save(project);

    InternalProjectDtoImpl internalProjectDto = project.toInternalProjectDtoImpl(0D);

    // Send topic to subscription service
    projectProducerService.send(
        new NewProjectSubscriptionRequestDto(project.toExternalProjectDto()));

    // Cache to redis
    projectRedisService.createProject(internalProjectDto);

    // return project.toInternalProjectDto();

    // redisTemplate.opsForValue().set(DONOR_CACHE_PREFIX + request.getUserId(), new
    // DonorDTO(donor));
    //
    // addToRedisZSet(donor);

    return project;
  }

  @Override
  @Transactional
  public InternalProjectDto getProjectById(String projectId) {

    // Check existed in redis first
    InternalProjectDtoImpl redisInternalProjectDtoImpl = projectRedisService.findOneById(projectId);
    if (!Objects.isNull(redisInternalProjectDtoImpl)) {
      logger.info("[GetProjectById] From Redis with <3: {}", projectId);
      return redisInternalProjectDtoImpl;
    }

    Optional<Project> optionalProject = projectRepository.findById(UUID.fromString(projectId));
    Optional<Project> optionalShardedProject = projectShardService.getProjectById(projectId);
    Project projectDto = new Project();

    if (optionalProject.isEmpty() && optionalShardedProject.isEmpty()) {
      throw new NotFoundProjectException();
    }

    if (optionalShardedProject.isEmpty()) {
      projectDto = optionalProject.get();
    }

    if (optionalProject.isEmpty()) {
      projectDto = optionalShardedProject.get();
    }

    InternalProjectDtoImpl internalProjectDtoImpl = toProjectInternalDto(projectDto);

    projectRedisService.cacheById(internalProjectDtoImpl);

    logger.info("[GetProjectById] From DB with <3: {}", projectId);

    return internalProjectDtoImpl;
  }

  @Override
  public Page<InternalProjectDto> searchProjects(
      Integer pageNo, Integer pageSize, SearchProjectsDto searchProjectsDto) {

    Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

    if (searchProjectsDto.getStatus() != StatusType.COMPLETED
        && searchProjectsDto.getStatus() != StatusType.DELETED) {

      Page<Project> projects = projectRepository.findProjectsByQuery(searchProjectsDto, pageable);
      return new PageImpl<>(toProjectsInternalDto(projects), pageable, projects.getTotalPages());
    }

    Page<Project> projects = projectShardService.findAllByQuery(searchProjectsDto, pageable);
    return new PageImpl<>(toProjectsInternalDto(projects), pageable, projects.getTotalPages());
  }

  private List<InternalProjectDto> toProjectsInternalDto(Page<Project> projects) {
    return projects.stream().map(this::toProjectInternalDto).collect(Collectors.toList());
  }

  private InternalProjectDtoImpl toProjectInternalDto(Project project) {
    List<String> projectIdList = Arrays.asList(project.getId().toString());

    DonationsDto getDonationsByProjectIdDto =
        projectProducerService.sendAndReceive(
            new GetDonationsByProjectIdDto(project.getId().toString()));

    InternalProjectDtoImpl internalProjectDto = project.toInternalProjectDtoImpl(0D);

    try {
      // TODO: Add videos and images query
      GetMediaByProjectIdResponseDto getMediaByProjectIdResponseDto =
          projectProducerService.sendAndReceive(new GetMediaByProjectIdRequestDto(projectIdList));

      // Add media to project
      addMediaListToProject(
          internalProjectDto,
          getMediaByProjectIdResponseDto.getMediaListDtoList().getFirst().getMediaDtoList());

      return internalProjectDto;
    } catch (Exception e) {
      logger.error("Error getting media for projects #{}", projectIdList, e);
    }

    if (getDonationsByProjectIdDto.getDonations() == null || getDonationsByProjectIdDto == null) {
      return internalProjectDto;
    }

    InternalProjectDtoImpl internalProjectDto1 =
        project.toInternalProjectDtoImpl(
            getDonationsByProjectIdDto.getDonations().stream()
                .map(DonationDto::getAmount)
                .reduce(0.0, Double::sum));

    internalProjectDto1.setMediaDtoList(internalProjectDto.getMediaDtoList());
    return internalProjectDto1;
  }

  @Override
  public List<String> searchProjectsId(SearchProjectsDto searchProjectsDto) {
    if (searchProjectsDto.getStatus().getValue().equalsIgnoreCase("APPROVED")) {
      return projectRepository.findProjectsIdByQuery(searchProjectsDto);
    }
    return projectShardService.findAllFilteredCompletedProject(
        searchProjectsDto.getCategoryTypes(), searchProjectsDto.getCountryIsoCodes());
  }

  @Override
  @Transactional
  public InternalProjectDto updateProjectDetails(
      String projectId, UpdateProjectDto updateProjectDto) {
    // If project not found
    Optional<Project> existedOptionalProject =
        projectRepository.findById(UUID.fromString(projectId));

    if (existedOptionalProject.isEmpty()) {
      throw new NotFoundProjectException();
    }

    Project project = existedOptionalProject.get();

    // Check date time is pass or not
    ZonedDateTime currentDateTime = ZonedDateTime.now();
    if (currentDateTime.isAfter(project.getStartTime())
        || currentDateTime.isAfter(project.getEndTime())) {
      throw new InvalidProjectException(
          "Start time and end time must not before the current date time");
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
    Optional<Project> existedOptionalProject =
        projectRepository.findById(UUID.fromString(projectId));

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
    Optional<Project> existedOptionalProject =
        projectRepository.findById(UUID.fromString(projectId));

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
    Optional<Project> existedOptionalProject =
        projectRepository.findById(UUID.fromString(projectId));

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
    // try {

    // Optional<Project> existedOptionalProject =
    // projectRepository.findById(UUID.fromString(projectId));

    // if (existedOptionalProject.isEmpty()) {
    // throw new NotFoundProjectException();
    // }

    // Project project = existedOptionalProject.get();

    // // Ensure project status is HALTED before deletion
    // if (!project.getStatusType().equals(StatusType.HALTED)) {
    // throw new InvalidProjectException("Project can be deleted if status is
    // HALTED");
    // }

    // // Set status to DELETED
    // project.setStatusType(StatusType.DELETED);
    // project = projectRepository.save(project);

    // return project;
    // } catch (Exception e) {
    // e.printStackTrace();
    // }

    // return null;

    try {

      boolean result =
          projectShardService.moveProjectFromProjectShardToProjectDeletedShard(projectId);
      if (result) {
        System.out.println("ok");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  @Override
  @Transactional
  public InternalProjectDto completeProject(String projectId) {
    // // If project not found
    // Optional<Project> existedOptionalProject =
    // projectRepository.findById(UUID.fromString(projectId));

    // if (existedOptionalProject.isEmpty()) {
    // throw new NotFoundProjectException();
    // }

    // Project project = existedOptionalProject.get();

    // // If project status is not HALTED
    // if (!project.getStatusType().equals(StatusType.APPROVED)) {
    // throw new InvalidProjectException("Project can be completed if status is
    // APPROVED");
    // }

    // project.setStatusType(StatusType.COMPLETED);
    // project = projectRepository.save(project);

    // return project;

    try {

      boolean result =
          projectShardService.moveProjectFromProjectShardToProjectCompletedShard(projectId);
      if (result) {
        System.out.println("ok");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return null;
  }

  @Override
  public GetProjectByCharityIdResponseDto getProjectByCharityId(
      GetProjectByCharityIdRequestDto requestDto) {
    String charitanId = requestDto.getCharityId();

    // List<String> additionalProjectStatusList =
    // requestDto.getAdditionalProjectStatusList();
    List<String> shardList = requestDto.getShardList();

    List<Project> projects = projectRepository.findAllByCharityId(charitanId);

    List<Project> otherShardProjects =
        projectShardService.findAllByCharitanId(shardList, charitanId);

    List<ExternalProjectDto> externalProjectDtoList =
        Stream.concat(projects.stream(), otherShardProjects.stream())
            .map(Project::toExternalProjectDto)
            .toList();

    return new GetProjectByCharityIdResponseDto(externalProjectDtoList);
  }

  @Override
  public void handleUpdateProjectMedia(
      UpdateProjectMediaDto.UpdateProjectMediaRequestDto requestDto) {
    // Get from cache redis
    InternalProjectDtoImpl internalProjectDto =
        projectRedisService.findOneById(requestDto.getProjectId());

    if (Objects.isNull(internalProjectDto)) {
      // Query from db again
      Optional<Project> optionalProject =
          projectRepository.findById(UUID.fromString(requestDto.getProjectId()));
      if (optionalProject.isEmpty()) {
        return;
      }

      // Get project
      internalProjectDto = optionalProject.get().toInternalProjectDtoImpl(0D);
    }

    // Add new image list to internalProjectDto
    addMediaListToProject(internalProjectDto, requestDto.getMediaDtoList());

    // Cache new object to redis
    projectRedisService.cacheById(internalProjectDto);
  }

  @Override
  public Page<InternalProjectDtoImpl> getMyProjects(String status, int page, int limit)
      throws Exception {
    AuthModel authModel = AuthUtils.getUserDetails();

    if (Objects.isNull(authModel)) {
      throw new Exception("Cannot get authModel");
    }

    String userId = authModel.getUsername();
    String role = authModel.getAuthorities().stream().toList().getFirst().getAuthority();

    System.out.println("Role: " + role);

    // Find by userId in redis
    // List<InternalProjectDtoImpl> existingProjectDtoList =
    // projectRedisService.findListByUserId(userId);
    // if (!existingProjectDtoList.isEmpty()) {
    // return existingProjectDtoList;
    // }

    List<Project> projectList = new ArrayList<>();
    Pageable pageable = PageRequest.of(page, limit);
    Page<InternalProjectDtoImpl> projectDtoPage;

    // If role is charity
    if (role.equals("CHARITY")) {
      StatusType statusType = StatusType.fromValue(status);

      if (statusType != StatusType.DELETED && statusType != StatusType.COMPLETED) {
        // Search in main shard
        projectDtoPage =
            projectRepository.findByCharityId(userId, pageable).map((this::toProjectInternalDto));
      } else {
        // Search in other shards
        projectDtoPage =
            projectShardService
                .findByCharityId(userId, statusType, pageable)
                .map(this::toProjectInternalDto);
      }

      return projectDtoPage;
    }

    // Add donor later

    return new PageImpl<>(new ArrayList<>(), pageable, 0);
  }
}
