package ace.charitan.project.internal.project.service;

import ace.charitan.project.internal.project.controller.ProjectRequestBody.SearchProjectsDto;
import ace.charitan.project.internal.project.exception.ProjectException.InvalidProjectException;
import ace.charitan.project.internal.project.exception.ProjectException.NotFoundProjectException;
import ace.charitan.project.internal.project.service.ProjectEnum.CategoryType;
import ace.charitan.project.internal.project.service.ProjectEnum.StatusType;
import jakarta.transaction.Transactional;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
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

  // Utility method for Optional query
  private <T> Optional<T> queryForOptional(
      JdbcTemplate jdbcTemplate, String sql, RowMapper<T> rowMapper, Object... args) {
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
    Optional<Project> completedProject =
        queryForOptional(projectCompletedJdbcTemplate, SQL, rowMapper, projectId);

    // If not found in completed, try deleted projects
    return completedProject.isPresent()
        ? completedProject
        : queryForOptional(projectDeletedJdbcTemplate, SQL, rowMapper, projectId);
  }

  private void appendQueryConditions(
      SearchProjectsDto searchProjectsDto,
      StringBuilder rowCountSql,
      StringBuilder sqlQuery,
      List<Object> params) {

    if (searchProjectsDto.getCategoryTypes() != null
        && !searchProjectsDto.getCategoryTypes().isEmpty()) {

      String inClause =
          String.join(",", Collections.nCopies(searchProjectsDto.getCategoryTypes().size(), "?"));
      rowCountSql.append(String.format(" AND category_type IN (%s)", inClause));
      sqlQuery.append(String.format(" AND category_type IN (%s)", inClause));
      params.addAll(searchProjectsDto.getCategoryTypes().stream().map(Enum::toString).toList());
    }

    if (searchProjectsDto.getCountryIsoCodes() != null
        && !searchProjectsDto.getCountryIsoCodes().isEmpty()) {

      String inClause =
          String.join(",", Collections.nCopies(searchProjectsDto.getCountryIsoCodes().size(), "?"));
      rowCountSql.append(String.format(" AND country_iso_code IN (%s)", inClause));
      sqlQuery.append(String.format(" AND country_iso_code IN (%s)", inClause));
      params.addAll(searchProjectsDto.getCountryIsoCodes());
    }

    if (searchProjectsDto.getName() != null && !searchProjectsDto.getName().isEmpty()) {
      rowCountSql.append(" AND LOWER(title) LIKE LOWER(CONCAT('%', ?, '%'))");
      sqlQuery.append(" AND LOWER(title) LIKE LOWER(CONCAT('%', ?, '%'))");
      params.add(searchProjectsDto.getName());
    }
  }

  Page<Project> findAllByQuery(SearchProjectsDto searchProjectsDto, Pageable pageable) {
    StringBuilder rowCountSql =
        new StringBuilder("SELECT count(1) AS row_count FROM project WHERE 1=1");
    StringBuilder sqlQuery = new StringBuilder("SELECT * FROM project WHERE 1=1");
    List<Object> params = new ArrayList<>();

    // Build query conditions
    appendQueryConditions(searchProjectsDto, rowCountSql, sqlQuery, params);

    // Add pagination
    sqlQuery
        .append(" LIMIT ")
        .append(pageable.getPageSize())
        .append(" OFFSET ")
        .append(pageable.getOffset());

    // Choose template based on status
    JdbcTemplate jdbcTemplate =
        searchProjectsDto.getStatus() == StatusType.COMPLETED
            ? projectCompletedJdbcTemplate
            : projectDeletedJdbcTemplate;

    System.out.println(params);
    // Execute count query
    int total =
        jdbcTemplate.queryForObject(
            rowCountSql.toString(), params.toArray(), (rs, rowNum) -> rs.getInt(1));

    System.out.println(total);

    // Execute main query
    List<Project> projects =
        jdbcTemplate.query(sqlQuery.toString(), params.toArray(), new ProjectRowMapper());

    return new PageImpl<>(projects, pageable, total);
  }

  List<Project> findAllByCharitanId(List<String> shardList, String charitanId) {
    List<Project> deletedProjectList = new ArrayList<>();
    List<Project> completedProjectList = new ArrayList<>();

    if (shardList.contains("PROJECT_DELETED")) {
      deletedProjectList =
          projectDeletedJdbcTemplate.query(
              "SELECT * FROM project where charity_id = ?", new ProjectRowMapper(), charitanId);
    }

    if (shardList.contains("PROJECT_COMPLETED")) {
      completedProjectList =
          projectCompletedJdbcTemplate.query(
              "SELECT * FROM project where charity_id = ?", new ProjectRowMapper(), charitanId);
    }

    return Stream.concat(deletedProjectList.stream(), completedProjectList.stream()).toList();
  }

  @Transactional
  boolean moveProjectFromProjectShardToProjectDeletedShard(String id) {
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

