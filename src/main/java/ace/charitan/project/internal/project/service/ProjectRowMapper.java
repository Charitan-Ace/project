package ace.charitan.project.internal.project.service;

import ace.charitan.project.internal.project.service.ProjectEnum.CategoryType;
import ace.charitan.project.internal.project.service.ProjectEnum.StatusType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import org.springframework.jdbc.core.RowMapper;

class ProjectRowMapper implements RowMapper<Project> {
  @Override
  public Project mapRow(ResultSet rs, int rowNum) throws SQLException {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss ZZ");

    Project project = new Project();
    project.setId(UUID.fromString(rs.getString("id")));
    project.setTitle(rs.getString("title"));
    project.setDescription(rs.getString("description"));
    project.setGoal(rs.getDouble("goal"));
    System.out.println("Testing something here " + project.getGoal());
    project.setStartTime(rs.getTimestamp("start_time").toLocalDateTime().atZone(ZoneId.of("UTC")));
    project.setEndTime(rs.getTimestamp("end_time").toLocalDateTime().atZone(ZoneId.of("UTC")));
    project.setStatusType(StatusType.fromValue(rs.getString("status_type")));
    project.setCategoryType(CategoryType.fromValue(rs.getString("category_type")));
    project.setCountryIsoCode(rs.getString("country_iso_code"));
    project.setCharityId(rs.getString("charity_id"));

    return project;
  }
}
