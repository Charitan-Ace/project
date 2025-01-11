package ace.charitan.project.internal.project.service;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
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
    boolean moveProjectFromProjectShardToProjectCompletedShard(String id) {
        Map<String, Object> project = projectJdbcTemplate.queryForMap(
                "SELECT * FROM project where id = CAST(? AS UUID)",
                id);

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
