package ace.charitan.project.config.db;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import ace.charitan.project.internal.project.service.ShardRoutingDataSource;

@Configuration
class RoutingDataSourceConfig {

    @Bean
    DataSource routingDataSource(
            @Qualifier("projectDataSource") DataSource projectDataSource,
            @Qualifier("projectDeletedDataSource") DataSource projectDeletedDataSource,
            @Qualifier("projectCompletedDataSource") DataSource projectCompletedDataSource) {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("PROJECT", projectDataSource);
        targetDataSources.put("PROJECT_DELETED", projectDeletedDataSource);
        targetDataSources.put("PROJECT_COMPLETED", projectCompletedDataSource);

        ShardRoutingDataSource routingDataSource = new ShardRoutingDataSource();
        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(projectDataSource);
        return routingDataSource;
    }

    @Primary
    @Bean
    DataSource dataSource(DataSource routingDataSource) {
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }
}
