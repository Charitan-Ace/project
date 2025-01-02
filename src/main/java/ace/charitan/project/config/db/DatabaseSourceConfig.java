package ace.charitan.project.config.db;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
class DatabaseSourceConfig {
    @Bean(name = "projectSource")
    @ConfigurationProperties("spring.datasource.project")
   DataSourceProperties projectDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "projectDeletedSource")
    @ConfigurationProperties("spring.datasource.deleted")
    DataSourceProperties projectDeletedDataSourceProperties() {
        return new DataSourceProperties();
    }

    @PostConstruct
    void checkDataSource() {
        System.out.println("Project DataSource URL: " + projectDataSourceProperties().toString());
    }
}
