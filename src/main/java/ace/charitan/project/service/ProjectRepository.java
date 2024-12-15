package ace.charitan.project.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ace.charitan.project.internal.InternalProjectDto;

@Repository
interface ProjectRepository extends JpaRepository<InternalProjectDto, Long> {
}
