package ace.charitan.project.internal.project.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ace.charitan.project.internal.project.dto.project.InternalProjectDto;

@Repository
interface ProjectRepository extends JpaRepository<Project, Long> {
    Page<InternalProjectDto> findByCountryIsoCode(String countryIsoCode, Pageable pageable);
}
