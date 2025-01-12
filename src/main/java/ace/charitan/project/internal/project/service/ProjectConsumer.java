package ace.charitan.project.internal.project.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import ace.charitan.common.dto.project.ProjectApproveDto;
import ace.charitan.common.dto.project.ProjectHaltDto;
import ace.charitan.common.dto.project.GetProjectByCharityIdDto.GetProjectByCharityIdRequestDto;
import ace.charitan.common.dto.project.GetProjectByCharityIdDto.GetProjectByCharityIdResponseDto;

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
    public ProjectHaltDto haltListener(ProjectHaltDto dto) {
        logger.info("Received halt request for project {}", dto.id());
        projectService.haltProject(dto.id());
        // TODO: improves return, error handling
        return dto;
    }

    @KafkaListener(topics = "project.approve")
    @SendTo
    public ProjectApproveDto approveListener(ProjectApproveDto dto) {
        logger.info("Received approve request for project {}", dto.getId());
        projectService.approveProject(dto.getId());
        // TODO: improves return, error handling
        return dto;
    }

    @KafkaListener(topics = "project.get-all-projects-by-charitan-id")
    @SendTo(KafkaHeaders.REPLY_TOPIC)
    GetProjectByCharityIdResponseDto handleGetProjectByCharitanId(GetProjectByCharityIdRequestDto requestDto) {
        System.out.println(requestDto.getCharityId());
        GetProjectByCharityIdResponseDto responseDto = projectService.getProjectByCharityId(requestDto);
        System.out.println(responseDto);
        return responseDto;
    }
}
