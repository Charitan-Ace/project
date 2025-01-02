package ace.charitan.project.config.db;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import jakarta.annotation.PostConstruct;

@Configuration
class ProjectDataSourceConfig {
    @Primary
    @Bean(name = "projectDataSourceProperties")
    @ConfigurationProperties("spring.datasource.project")
    DataSourceProperties projectDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "projectDataSource")
    @ConfigurationProperties("spring.datasource.project")
    public DataSource projectDataSource(@Qualifier("projectDataSourceProperties") DataSourceProperties properties) {
        return properties
                .initializeDataSourceBuilder()
                .build();
    }

    @PostConstruct
    void print() {
        System.out.println(projectDataSourceProperties().toString());
    }

}
