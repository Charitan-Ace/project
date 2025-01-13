package ace.charitan.project.internal.project.service;

import ace.charitan.common.dto.project.*;
import ace.charitan.common.dto.project.GetProjectByCharityIdDto.GetProjectByCharityIdRequestDto;
import ace.charitan.common.dto.project.GetProjectByCharityIdDto.GetProjectByCharityIdResponseDto;
import ace.charitan.project.internal.project.controller.ProjectRequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
class ProjectConsumer {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final InternalProjectService projectService;

    ProjectConsumer(
            InternalProjectService projectService) {
        this.projectService = projectService;
    }

    @KafkaListener(topics = "project.halt")
    @SendTo
    public ExternalProjectDto haltListener(ProjectHaltDto dto) {
        logger.info("Received halt request for project {}", dto.id());
        var project = projectService.haltProject(dto.id());
        return new ExternalProjectDto(
                dto.id(),
                project.getTitle(),
                project.getDescription(),
                project.getGoal(),
                project.getStartTime(),
                project.getEndTime(),
                project.getStatusType().toString(),
                project.getCategoryType().toString(),
                project.getCountryIsoCode(),
                project.getCharityId()
        );
    }

    @KafkaListener(topics = "project.approve")
    @SendTo
    public ExternalProjectDto approveListener(ProjectApproveDto dto) {
        logger.info("Received approve request for project {}", dto.getId());
        var project = projectService.approveProject(dto.getId());
        return new ExternalProjectDto(
                dto.getId(),
                project.getTitle(),
                project.getDescription(),
                project.getGoal(),
                project.getStartTime(),
                project.getEndTime(),
                project.getStatusType().toString(),
                project.getCategoryType().toString(),
                project.getCountryIsoCode(),
                project.getCharityId()
        );
    }

    @KafkaListener(topics = "project.get.id")
    @SendTo
    public ExternalProjectDto approveListener(GetProjectByIdDto dto) {
        logger.info("Received project {} request", dto.id());
        var project = projectService.getProjectById(dto.id());
        return new ExternalProjectDto(
                dto.id(),
                project.getTitle(),
                project.getDescription(),
                project.getGoal(),
                project.getStartTime(),
                project.getEndTime(),
                project.getStatusType().toString(),
                project.getCategoryType().toString(),
                project.getCountryIsoCode(),
                project.getCharityId()
        );
    }

    @KafkaListener(topics = "project.get-all-projects-by-charitan-id")
    @SendTo
    GetProjectByCharityIdResponseDto handleGetProjectByCharitanId(GetProjectByCharityIdRequestDto requestDto) {
        System.out.println(requestDto.getCharityId());
        GetProjectByCharityIdResponseDto responseDto = projectService.getProjectByCharityId(requestDto);
        System.out.println(responseDto);
        return responseDto;
    }

    @KafkaListener(topics = "project.update-project-media")
    void handleUpdateProjectMedia(UpdateProjectMediaDto.UpdateProjectMediaRequestDto requestDto) {
        projectService.handleUpdateProjectMedia(requestDto);
    }
}
