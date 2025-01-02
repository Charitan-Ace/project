package ace.charitan.project.config.db;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

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
    DataSource projectDataSource(@Qualifier("projectDataSourceProperties") DataSourceProperties properties) {
        return properties
                .initializeDataSourceBuilder()
                .build();
    }

    @Primary
    @Bean(name = "projectEntityManagerFactory")
    LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(EntityManagerFactoryBuilder builder,
            @Qualifier("projectDataSource") DataSource dataSource) {

        return builder.dataSource(dataSource)
                .packages("ace.charitan.project.internal.project.service")
                .persistenceUnit("project").build();
    }

    @PostConstruct
    void print() {
        System.out.println("Project datsource url " + projectDataSourceProperties().toString());
    }

}
