package ace.charitan.project.internal.project.service;

import ace.charitan.project.internal.project.controller.ProjectRequestBody.SearchProjectsDto;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ace.charitan.project.internal.project.dto.project.InternalProjectDto;

@Repository
interface ProjectRepository extends JpaRepository<Project, UUID> {
    @Query("select p from Project p where" +
        "(:#{#searchProjectsDto.categoryTypes} IS NULL OR p.categoryType IN :#{#searchProjectsDto.categoryTypes}) AND " +
        "(:#{#searchProjectsDto.statuses} IS NULL OR p.statusType IN :#{#searchProjectsDto.statuses}) AND " +
        "(:#{#searchProjectsDto.name} IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :#{#searchProjectsDto.name}, '%'))) AND " +
        "(:#{#searchProjectsDto.countryIsoCodes} IS NULL OR p.countryIsoCode IN :#{#searchProjectsDto.countryIsoCodes}) AND " +
        "(:#{#searchProjectsDto.startTime} IS NULL OR p.startTime >= :#{#searchProjectsDto.startTime}) AND " +
        "(:#{#searchProjectsDto.endTime} IS NULL OR p.startTime <= :#{#searchProjectsDto.endTime})")
    Page<InternalProjectDto> findProjectsByQuery(SearchProjectsDto searchProjectsDto, Pageable pageable);
  
    Page<InternalProjectDto> findByCountryIsoCode(String countryIsoCode, Pageable pageable);

    List<Project> findAllByCharityId(String charitanId);
}
