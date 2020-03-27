package xyz.srclab.common.cache;

import xyz.srclab.common.cache.threadlocal.ThreadLocalCache;

import java.time.Duration;

public class CacheHelper {

    public static <K, V> Cache<K, V> newThreadLocalCache() {
        return new ThreadLocalCache<>();
    }

    public static <K, V> Cache<K, V> newThreadLocalCache(long defaultExpirationPeriodMillis) {
        return newThreadLocalCache(Duration.ofMillis(defaultExpirationPeriodMillis));
    }

    public static <K, V> Cache<K, V> newThreadLocalCache(Duration defaultExpirationPeriod) {
        return new ThreadLocalCache<>(defaultExpirationPeriod);
    }
}
