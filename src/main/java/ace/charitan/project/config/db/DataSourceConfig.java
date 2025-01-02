package ace.charitan.project.config.db;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// import jakarta.activation.DataSource;

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.project.url}")
    private String url;

    @Bean(name = "projectDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.project")
    DataSource projectDataSource() {
        System.out.println("project url" + url);
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "projectDeletedDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.deleted")
    DataSource projectDeletedDataSource() {
        return DataSourceBuilder.create().build();
    }
}
