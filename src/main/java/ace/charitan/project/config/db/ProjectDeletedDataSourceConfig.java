package ace.charitan.project.config.db;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

@Configuration
public class ProjectDeletedDataSourceConfig {
    @Bean(name = "projectDeletedDataSourceProperties")
    @ConfigurationProperties("spring.datasource.deleted")
    DataSourceProperties projectDeletedDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "projectDeletedDataSource")
    @ConfigurationProperties("spring.datasource.deleted")
    DataSource projectDeletedDataSource(
            @Qualifier("projectDeletedDataSourceProperties") DataSourceProperties properties) {
        return properties
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean(name = "projectDeletedEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(EntityManagerFactoryBuilder builder,
            @Qualifier("projectDeletedDataSource") DataSource dataSource) {

        return builder.dataSource(dataSource)
                .packages("ace.charitan.project.internal.project.service")
                .persistenceUnit("project").build();
    }
}
