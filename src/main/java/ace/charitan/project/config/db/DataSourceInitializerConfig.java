package ace.charitan.project.config.db;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

@Configuration
public class DataSourceInitializerConfig {

    @Bean
    public DataSourceInitializer projectDataSourceInitializer(
            @Qualifier("projectDataSource") DataSource dataSource) {
        return createInitializer(dataSource, "classpath:sql/project-data.sql");
    }

    // @Bean
    // public DataSourceInitializer projectDeletedDataSourceInitializer(
    //         @Qualifier("projectDeletedDataSource") DataSource dataSource) {
    //     return createInitializer(dataSource, "classpath:sql/project-deleted-data.sql");
    // }

    private DataSourceInitializer createInitializer(DataSource dataSource, String scriptLocation) {
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        databasePopulator.addScript(new ClassPathResource(scriptLocation));
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(databasePopulator);
        return initializer;
    }
}