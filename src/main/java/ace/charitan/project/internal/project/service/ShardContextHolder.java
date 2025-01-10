package ace.charitan.project.internal.project.service;

class ShardContextHolder {

    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    public static void setCurrentShard(String shardKey) {
        contextHolder.set(shardKey);
    }

    public static String getCurrentShard() {
        return contextHolder.get();
    }

    public static void clear() {
        contextHolder.remove();
    }
}