package ace.charitan.project.internal.project.service;

import ace.charitan.common.dto.project.ProjectApproveDto;
import ace.charitan.common.dto.project.ProjectHaltDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
class ProjectConsumer {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final InternalProjectService projectService;

    private ProjectConsumer(
            InternalProjectService projectService
    ) {
        this.projectService = projectService;
    }

    @KafkaListener(topics = "project.halt")
    @SendTo
    public ProjectHaltDto haltListener(ProjectHaltDto dto) {
        logger.info("Received halt request for project {}", dto.getId());
        projectService.haltProject(dto.getId());
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
}
