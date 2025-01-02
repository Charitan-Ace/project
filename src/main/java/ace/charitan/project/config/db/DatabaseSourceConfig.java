package ace.charitan.project.config.db;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class DatabaseSourceConfig {
    @Bean(name = "projectSource")
    @ConfigurationProperties("spring.datasource.project")
    public DataSourceProperties projectDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "projectDeletedSource")
    @ConfigurationProperties("spring.datasource.deleted")
    public DataSourceProperties projectDeletedDataSourceProperties() {
        return new DataSourceProperties();
    }
}
