package ace.charitan.project.config.db;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class ProjectDeletedDataSourceConfig {
    @Bean(name = "projectDeletedDataSourceProperties")
    @ConfigurationProperties("spring.datasource.deleted")
    DataSourceProperties projectDeletedDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "projectDeletedDataSource")
    @ConfigurationProperties("spring.datasource.deleted")
     DataSource projectDeletedDataSource(@Qualifier("projectDeletedDataSourceProperties") DataSourceProperties properties) {
        return properties
                .initializeDataSourceBuilder()
                .build();
    }
}
