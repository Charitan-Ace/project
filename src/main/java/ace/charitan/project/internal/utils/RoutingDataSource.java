package ace.charitan.project.internal.utils;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

class RoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return ShardContextHolder.getCurrentShard();
    }
}