package ace.charitan.project.internal.project.service;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ace.charitan.common.dto.project.UpdateProjectMediaDto;
import org.apache.kafka.common.metrics.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import ace.charitan.common.dto.media.ExternalMediaDto;
import ace.charitan.common.dto.media.GetMediaByProjectIdRequestDto;
import ace.charitan.common.dto.media.GetMediaByProjectIdResponseDto;
import ace.charitan.common.dto.project.ExternalProjectDto;
import ace.charitan.common.dto.project.GetProjectByCharityIdDto.GetProjectByCharityIdRequestDto;
import ace.charitan.common.dto.project.GetProjectByCharityIdDto.GetProjectByCharityIdResponseDto;
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

@Service
class ProjectServiceImpl implements InternalProjectService {

  @Value("${data.charitan-id}")
  private String DEFAULT_CHARITAN_ID;

  @Autowired
  private ProjectRepository projectRepository;

  @Autowired
  private ProjectShardService projectShardService;

  @Autowired
  private ProjectProducerService projectProducerService;

  @Autowired
  private ProjectRedisService projectRedisService;

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

  private void addMediaListToProject(InternalProjectDtoImpl project, List<ExternalMediaDto> mediaDtoList) {
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

    InternalProjectDtoImpl internalProjectDto = project.toInternalProjectDtoImpl();

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
      System.out.println("[GetProjectById] From Redis with <3: " + projectId);
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

    // TODO: Add videos and images query
    List<String> projectIdList = Arrays.asList(projectDto.getId().toString());
    GetMediaByProjectIdResponseDto getMediaByProjectIdResponseDto = projectProducerService
        .sendAndReceive(new GetMediaByProjectIdRequestDto(projectIdList));

    InternalProjectDtoImpl internalProjectDtoImpl = projectDto.toInternalProjectDtoImpl();

    // Add media to project
    addMediaListToProject(
        internalProjectDtoImpl,
        getMediaByProjectIdResponseDto.getMediaListDtoList().getFirst().getMediaDtoList());

    // Cache project to redis
    projectRedisService.cacheById(internalProjectDtoImpl);

    System.out.println("[GetProjectById] From DB with <3: " + projectId);

    // Convert to impl
    return internalProjectDtoImpl;
  }

  @Override
  public Page<InternalProjectDto> searchProjects(
      Integer pageNo, Integer pageSize, SearchProjectsDto searchProjectsDto) {

    Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

    if (searchProjectsDto.getStatus() != StatusType.COMPLETED
        && searchProjectsDto.getStatus() != StatusType.DELETED) {
      return projectRepository.findProjectsByQuery(searchProjectsDto, pageable);
    }

    Page<Project> projects = projectShardService.findAllByQuery(searchProjectsDto, pageable);
    return new PageImpl<>(
        projects.stream()
            .map(
                project -> {
                  List<String> projectIdList = Arrays.asList(project.getId().toString());
                  GetMediaByProjectIdResponseDto getMediaByProjectIdResponseDto = projectProducerService.sendAndReceive(
                      new GetMediaByProjectIdRequestDto(projectIdList));

                  InternalProjectDtoImpl internalProjectDto = project.toInternalProjectDtoImpl();

                  // Add media to project
                  addMediaListToProject(
                      internalProjectDto,
                      getMediaByProjectIdResponseDto
                          .getMediaListDtoList()
                          .get(0)
                          .getMediaDtoList());

                  return internalProjectDto;
                })
            .collect(Collectors.toList()),
        pageable,
        projects.getTotalPages());
  }

  @Override
  @Transactional
  public InternalProjectDto updateProjectDetails(
      String projectId, UpdateProjectDto updateProjectDto) {
    // If project not found
    Optional<Project> existedOptionalProject = projectRepository.findById(UUID.fromString(projectId));

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

      boolean result = projectShardService.moveProjectFromProjectShardToProjectDeletedShard(projectId);
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

      boolean result = projectShardService.moveProjectFromProjectShardToProjectCompletedShard(projectId);
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

    List<Project> otherShardProjects = projectShardService.findAllByCharitanId(shardList, charitanId);

    List<ExternalProjectDto> externalProjectDtoList = Stream.concat(projects.stream(), otherShardProjects.stream())
        .map(Project::toExternalProjectDto)
        .toList();

    return new GetProjectByCharityIdResponseDto(externalProjectDtoList);
  }

  @Override
  public void handleUpdateProjectMedia(UpdateProjectMediaDto.UpdateProjectMediaRequestDto requestDto) {
    // Get from cache redis
    InternalProjectDtoImpl internalProjectDto = projectRedisService.findOneById(requestDto.getProjectId());

    if (Objects.isNull(internalProjectDto)) {
      // Query from db again
      Optional<Project> optionalProject = projectRepository.findById(UUID.fromString(requestDto.getProjectId()));
      if (optionalProject.isEmpty()) {
        return;
      }

      // Get project
      internalProjectDto = optionalProject.get().toInternalProjectDtoImpl();
    }

    // Add new image list to internalProjectDto
    addMediaListToProject(internalProjectDto, requestDto.getMediaDtoList());

    // Cache new object to redis
    projectRedisService.cacheById(internalProjectDto);
  }

  @Override
  public Page<InternalProjectDtoImpl> getMyProjects(String status, int page, int limit) throws Exception {
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
        projectDtoPage = projectRepository.findByCharityId(userId, pageable)
            .map(Project::toInternalProjectDtoImpl);
      } else {
        // Search in other shards
        projectDtoPage = projectShardService.findByCharityId(userId, statusType, pageable)
            .map(Project::toInternalProjectDtoImpl);
      }

      return projectDtoPage;
    }

    // Add donor later

    

    return new PageImpl<>(new ArrayList<>(), pageable, 0);

  }

}
