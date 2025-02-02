package ace.charitan.project.internal.project.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ace.charitan.project.internal.project.controller.ProjectRequestBody.SearchProjectsDto;
import ace.charitan.project.internal.project.dto.project.InternalProjectDto;
import ace.charitan.project.internal.project.service.ProjectEnum.StatusType;

@Repository
interface ProjectRepository extends JpaRepository<Project, UUID> {
        @Query("select p from Project p where" +
                        "(:#{#searchProjectsDto.categoryTypes} IS NULL OR p.categoryType IN :#{#searchProjectsDto.categoryTypes}) AND "
                        +
                        "(:#{#searchProjectsDto.status} IS NULL OR p.statusType IN :#{#searchProjectsDto.status}) AND "
                        +
                        "(:#{#searchProjectsDto.name} IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :#{#searchProjectsDto.name}, '%'))) AND "
                        +
                        "(:#{#searchProjectsDto.countryIsoCodes} IS NULL OR p.countryIsoCode IN :#{#searchProjectsDto.countryIsoCodes}) AND "
                        +
                        "(:#{#searchProjectsDto.startTime} IS NULL OR p.startTime >= :#{#searchProjectsDto.startTime}) AND "
                        +
                        "(:#{#searchProjectsDto.endTime} IS NULL OR p.startTime <= :#{#searchProjectsDto.endTime})")
        Page<Project> findProjectsByQuery(SearchProjectsDto searchProjectsDto, Pageable pageable);

        @Query("select p.id from Project p where" +
                        "(:#{#searchProjectsDto.categoryTypes} IS NULL OR p.categoryType IN :#{#searchProjectsDto.categoryTypes}) AND "
                        +
                        "(:#{#searchProjectsDto.status} IS NULL OR p.statusType IN :#{#searchProjectsDto.status}) AND "
                        +
                        "(:#{#searchProjectsDto.name} IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :#{#searchProjectsDto.name}, '%'))) AND "
                        +
                        "(:#{#searchProjectsDto.countryIsoCodes} IS NULL OR p.countryIsoCode IN :#{#searchProjectsDto.countryIsoCodes}) AND "
                        +
                        "(:#{#searchProjectsDto.startTime} IS NULL OR p.startTime >= :#{#searchProjectsDto.startTime}) AND "
                        +
                        "(:#{#searchProjectsDto.endTime} IS NULL OR p.startTime <= :#{#searchProjectsDto.endTime})")
        List<String> findProjectsIdByQuery(SearchProjectsDto searchProjectsDto);

        Page<InternalProjectDto> findByCountryIsoCode(String countryIsoCode, Pageable pageable);

        Page<Project> findByStatusTypeAndCharityId(ProjectEnum.StatusType statusType, String charityId,
                        Pageable pageable);

        Page<Project> findByCharityId(String charityId, Pageable pageable);

        Page<Project> findByStatusTypeAndIdIn(StatusType statusType, List<UUID> ids, Pageable pageable);

        List<Project> findAllByCharityId(String charitanId);
}
