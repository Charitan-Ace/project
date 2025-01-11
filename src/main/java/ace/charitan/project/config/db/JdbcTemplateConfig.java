package ace.charitan.project.config.db;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import jakarta.transaction.Transactional;

@Configuration
class JdbcTemplateConfig {
    @Bean(name = "projectJdbcTemplate")
    public JdbcTemplate projectJdbcTemplate(@Qualifier("projectDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "projectDeletedJdbcTemplate")
    public JdbcTemplate projectDeletedJdbcTemplate(@Qualifier("projectDeletedDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }


}
