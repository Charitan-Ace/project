package ace.charitan.project.service;

import ace.charitan.project.internal.dto.InternalProjectDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface ProjectRepository extends JpaRepository<InternalProjectDto, Long> {
}
