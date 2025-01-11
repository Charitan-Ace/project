package ace.charitan.project.internal.project.service;

import ace.charitan.project.internal.project.exception.ProjectException.InvalidProjectException;
import ace.charitan.project.internal.project.exception.ProjectException.NotFoundProjectException;
import ace.charitan.project.internal.project.service.ProjectEnum.CategoryType;
import ace.charitan.project.internal.project.service.ProjectEnum.StatusType;
import jakarta.transaction.Transactional;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

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

    class ProjectRowMapper implements RowMapper<Project> {

      @Override
      public Project mapRow(ResultSet rs, int rowNum) throws SQLException {
        Project project = new Project();
        project.setId(UUID.fromString(rs.getString("Id")));
        project.setTitle(rs.getString("title"));
        project.setGoal(rs.getDouble("goal"));
        project.setStartTime(
            rs.getTimestamp("start_time").toLocalDateTime().atZone(ZoneId.of("UTC")));
        project.setCharityId(rs.getString("charity_id"));
        project.setStatusType(StatusType.fromValue(rs.getString("status_type")));
        project.setCategoryType(CategoryType.fromValue(rs.getString("category_type")));
        project.setCountryIsoCode(rs.getString("country_iso_code"));
        return project;
      }
    }

  // Utility method for Optional query
  private <T> Optional<T> queryForOptional(JdbcTemplate jdbcTemplate, String sql, RowMapper<T> rowMapper, Object... args) {
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
//    final String DELETE_SQL = "SELECT * FROM project WHERE id = ?";

    // Create type-safe row mapper

    RowMapper<Project> rowMapper = new ProjectRowMapper();

    // Try completed projects first
    Optional<Project> completedProject = queryForOptional(
        projectCompletedJdbcTemplate,
        SQL,
        rowMapper,
        projectId
    );

    // If not found in completed, try deleted projects
    return completedProject.isPresent()
        ? completedProject
        : queryForOptional(projectDeletedJdbcTemplate, SQL, rowMapper, projectId);
  }


  @Transactional
  public boolean moveProjectFromProjectShardToProjectDeletedShard(String id) {
    Map<String, Object> project =
        projectJdbcTemplate.queryForMap("SELECT * FROM project where id = CAST(? AS UUID)", id);

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
    Map<String, Object> project =
        projectJdbcTemplate.queryForMap("SELECT * FROM project where id = CAST(? AS UUID)", id);

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
