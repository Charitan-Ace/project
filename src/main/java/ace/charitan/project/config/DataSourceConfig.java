package ace.charitan.project.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import jakarta.activation.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean(name = "ongoingDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.ongoing")
    public DataSource ongoingDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "completedDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.completed")
    public DataSource completedDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "pendingDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.pending")
    public DataSource pendingDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public DataSource routingDataSource(@Qualifier("ongoingDataSource") DataSource ongoingDataSource,
                                        @Qualifier("completedDataSource") DataSource completedDataSource,
                                        @Qualifier("pendingDataSource") DataSource pendingDataSource) {
        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put("ONGOING", ongoingDataSource);
        dataSourceMap.put("COMPLETED", completedDataSource);
        dataSourceMap.put("PENDING", pendingDataSource);

        AbstractRoutingDataSource routingDataSource = new RoutingDataSource();
        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(ongoingDataSource);
        return routingDataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource routingDataSource) {
        return new JdbcTemplate(routingDataSource);
    }
}