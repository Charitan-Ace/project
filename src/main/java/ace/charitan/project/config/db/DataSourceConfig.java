package ace.charitan.project.config.db;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import jakarta.annotation.PostConstruct;

// import jakarta.activation.DataSource;

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.project.url}")
    private String url;

    @Bean(name = "projectDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.project")
    // @Primary
    DataSource projectDataSource() {
        System.out.println("project url" + url);
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "projectDeletedDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.deleted")
    DataSource projectDeletedDataSource() {
        return DataSourceBuilder.create().build();
    }

    @PostConstruct
    public void logEnvironmentVariables() {
        System.out.println("SPRING_DATASOURCE_PROJECT_URL: " +
                System.getenv("SPRING_DATASOURCE_PROJECT_URL"));
        System.out
                .println("SPRING_DATASOURCE_PROJECT_USERNAME: " +
                        System.getenv("SPRING_DATASOURCE_PROJECT_USERNAME"));
        System.out
                .println("SPRING_DATASOURCE_PROJECT_PASSWORD: " +
                        System.getenv("SPRING_DATASOURCE_PROJECT_PASSWORD"));
    }

    @PostConstruct
    public void checkDataSource() {
        System.out.println("Project DataSource URL: " + projectDataSource().toString());
    }
}
