package ace.charitan.project.internal.project.service;

import ace.charitan.common.dto.project.UpdateProjectMediaDto;
import ace.charitan.project.internal.project.dto.project.InternalProjectDtoImpl;
import org.springframework.data.domain.Page;

import ace.charitan.common.dto.project.GetProjectByCharityIdDto.GetProjectByCharityIdRequestDto;
import ace.charitan.common.dto.project.GetProjectByCharityIdDto.GetProjectByCharityIdResponseDto;
import ace.charitan.project.internal.project.controller.ProjectRequestBody.CreateProjectDto;
import ace.charitan.project.internal.project.controller.ProjectRequestBody.SearchProjectsDto;
import ace.charitan.project.internal.project.controller.ProjectRequestBody.UpdateProjectDto;
import ace.charitan.project.internal.project.dto.project.InternalProjectDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface InternalProjectService {

    InternalProjectDto createProject(CreateProjectDto createProjectDto);

    Page<InternalProjectDto> searchProjects(Integer pageNo, Integer pageSize,
            SearchProjectsDto searchProjectsDto);

    List<String> searchProjectsId(SearchProjectsDto searchProjectsDto);

    InternalProjectDto getProjectById(String projectId);

    InternalProjectDto updateProjectDetails(String projectId, UpdateProjectDto updateProjectDto);

    InternalProjectDto approveProject(String projectId);

    InternalProjectDto haltProject(String projectId);

    InternalProjectDto resumeProject(String projectId);

    InternalProjectDto deleteProject(String projectId);

    InternalProjectDto completeProject(String projectId);

    GetProjectByCharityIdResponseDto getProjectByCharityId(GetProjectByCharityIdRequestDto requestDto);

    void handleUpdateProjectMedia(UpdateProjectMediaDto.UpdateProjectMediaRequestDto requestDto);

    Page<InternalProjectDtoImpl> getMyProjects(String statusType, int page, int limit) throws Exception;
}
