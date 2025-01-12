
package ace.charitan.project.internal.project.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import ace.charitan.project.internal.project.exception.ProjectException.InvalidProjectException;
import ace.charitan.project.internal.project.exception.ProjectException.NotFoundProjectException;
import jakarta.transaction.Transactional;

@Service
class ProjectShardService {

    @Autowired
    @Qualifier("projectJdbcTemplate")
    private JdbcTemplate projectJdbcTemplate;

    @Autowired
    @Qualifier("projectDeletedJdbcTemplate")
    private JdbcTemplate projectDeletedJdbcTemplate;

    @Autowired
    @Qualifier("projectCompletedJdbcTemplate")
    private JdbcTemplate projectCompletedJdbcTemplate;

    // Utility method for Optional query
    private <T> Optional<T> queryForOptional(JdbcTemplate jdbcTemplate, String sql, RowMapper<T> rowMapper,
            Object... args) {
        try {

            T result = jdbcTemplate.queryForObject(sql, args, rowMapper);
            return Optional.of(result);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Transactional
    public Optional<Project> getProjectById(String projectId) {
        final String SQL = "SELECT * FROM project WHERE id = CAST(? AS UUID)";
        // final String DELETE_SQL = "SELECT * FROM project WHERE id = ?";

        // Create type-safe row mapper

        RowMapper<Project> rowMapper = new ProjectRowMapper();

        // Try completed projects first
        Optional<Project> completedProject = queryForOptional(
                projectCompletedJdbcTemplate,
                SQL,
                rowMapper,
                projectId);

        // If not found in completed, try deleted projects
        return completedProject.isPresent()
                ? completedProject
                : queryForOptional(projectDeletedJdbcTemplate, SQL, rowMapper, projectId);
    }

    List<Project> findAllByCharitanId(List<String> shardList, String charitanId) {
        List<Project> deletedProjectList = new ArrayList<>();
        List<Project> completedProjectList = new ArrayList<>();

        if (shardList.contains("PROJECT_DELETED")) {
            deletedProjectList = projectDeletedJdbcTemplate.query(
                    "SELECT * FROM project where project.charity_id = ?",
                    new ProjectRowMapper(), charitanId);
        }

        if (shardList.contains("PROJECT_COMPLETED")) {
            completedProjectList = projectCompletedJdbcTemplate.query(
                    "SELECT * FROM project where project.charity_id = ?",
                    new ProjectRowMapper(), charitanId);
        }

        return Stream.concat(deletedProjectList.stream(), completedProjectList.stream()).toList();

    }

    @Transactional
    boolean moveProjectFromProjectShardToProjectDeletedShard(String id) {
        Map<String, Object> project = projectJdbcTemplate.queryForMap(
                "SELECT * FROM project where id = CAST(? AS UUID)",
                id);

        if (Objects.isNull(project)) {
            throw new NotFoundProjectException();
        }

        if (!project.get("status_type").equals("HALTED")) {
            throw new InvalidProjectException("Project can be deleted of status is HALTED");
        }

        projectDeletedJdbcTemplate.update(
                "INSERT INTO project (id, title, description, goal, start_time, end_time, status_type, category_type, country_iso_code, charity_id) VALUES (CAST(? AS UUID), ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                UUID.fromString(id),
                project.get("title"),
                project.get("description"),
                project.get("goal"),
                project.get("start_time"),
                project.get("end_time"),
                "DELETED",
                project.get("category_type"),
                project.get("country_iso_code"),
                project.get("charity_id"));

        projectJdbcTemplate.update("DELETE FROM project WHERE id = CAST(? AS UUID)", id);

        return true;
    }

    @Transactional
    public boolean moveProjectFromProjectShardToProjectCompletedShard(String id) {
        Map<String, Object> project = projectJdbcTemplate
                .queryForMap("SELECT * FROM project where id = CAST(? AS UUID)", id);

        if (Objects.isNull(project)) {
            throw new NotFoundProjectException();
        }

        if (!project.get("status_type").equals("APPROVED")) {
            throw new InvalidProjectException("Project can be completed of status is APPROVED");
        }

        projectCompletedJdbcTemplate.update(
                "INSERT INTO project (id, title, description, goal, start_time, end_time, status_type, category_type, country_iso_code, charity_id) VALUES (CAST(? AS UUID), ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                UUID.fromString(id),
                project.get("title"),
                project.get("description"),
                project.get("goal"),
                project.get("start_time"),
                project.get("end_time"),
                "COMPLETED",
                project.get("category_type"),
                project.get("country_iso_code"),
                project.get("charity_id"));

        projectJdbcTemplate.update("DELETE FROM project WHERE id = CAST(? AS UUID)", id);

        return true;
    }
}

// <<<<<<< trungngo21/get-project-across-db
