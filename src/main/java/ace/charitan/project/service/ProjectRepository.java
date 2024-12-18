package ace.charitan.project.service;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
interface ProjectRepository extends MongoRepository<Project, String> {
}
