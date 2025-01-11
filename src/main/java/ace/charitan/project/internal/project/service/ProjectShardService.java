package ace.charitan.project.internal.project.service;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.apache.kafka.common.Uuid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

@Service
class ProjectShardService {

    @Autowired
    @Qualifier("projectJdbcTemplate")
    private JdbcTemplate projectJdbcTemplate;

    @Autowired
    @Qualifier("projectDeletedJdbcTemplate")
    private JdbcTemplate projectDeletedJdbcTemplate;

    @Transactional
    boolean moveProjectFromProjectShardToProjectDeletedShard(String id) {
        Map<String, Object> project = projectJdbcTemplate.queryForMap("SELECT * FROM project where id = ?",
                id);

        if (Objects.isNull(project)) {
            return false;
        }

        projectDeletedJdbcTemplate.update(
                "INSERT INTO project (id, title, description, goal, start_time, end_time, status_type, category_type, country_iso_code, charity_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                id,
                project.get("title"),
                project.get("description"),
                project.get("goal"),
                project.get("start_time"),
                project.get("end_time"),
                "DELETED",
                project.get("category_type"),
                project.get("country_iso_code"),
                project.get("charity_id")
        );

        projectJdbcTemplate.update("DELETE FROM project WHERE id = ?", id);

        return true;
    }
}
