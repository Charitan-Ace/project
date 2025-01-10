package ace.charitan.project.config.db;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class DataSourceConfig {

    @Bean(name = "projectDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.project")
    public DataSource projectDataSource() {
        return DataSourceBuilder.create().build();
    }

    // @Bean(name = "projectDeletedDataSource")
    // @ConfigurationProperties(prefix = "spring.datasource.project-deleted")
    // public DataSource projectDeletedDataSource() {
    // return DataSourceBuilder.create().build();
    // }
}
