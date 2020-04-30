package xyz.srclab.common.cache;

import com.google.common.collect.MapMaker;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author sunqian
 */
final class CacheSupport {

    static final int DEFAULT_CONCURRENCY_LEVEL = 32;

    static <K, V> Map<K, V> newAutoCleanMap(int concurrencyLevel) {
        if (concurrencyLevel <= 1) {
            return new WeakHashMap<>();
        }
        return new MapMaker()
                .concurrencyLevel(concurrencyLevel)
                .weakKeys()
                .makeMap();
    }
}
