package ace.charitan.project.internal.project.service;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class ShardRoutingDataSource extends AbstractRoutingDataSource {

    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    public static void setCurrentShard(String shardKey) {
        contextHolder.set(shardKey);
    }

    public static void clearCurrentShard() {
        contextHolder.remove();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return contextHolder.get();
    }
}
