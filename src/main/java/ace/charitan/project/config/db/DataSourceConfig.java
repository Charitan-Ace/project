package ace.charitan.project.config.db;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
class DataSourceConfig {

    @Value("${spring.datasource.project.url}")
    private String url;

    @Autowired
    private Environment env;

    @Bean(name = "projectDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.project")
    public DataSource projectDataSource() {

        // System.out.println("URL " + url);
        // return DataSourceBuilder.create().build();

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("spring.datasource.project.driver-class-name"));
        dataSource.setUrl(env.getProperty("spring.datasource.project.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.project.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.project.password"));

        return dataSource;
    }

    // @Bean(name = "projectDeletedDataSource")
    // @ConfigurationProperties(prefix = "spring.datasource.project-deleted")
    // public DataSource projectDeletedDataSource() {
    // return DataSourceBuilder.create().build();
    // }
}
