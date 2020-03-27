package xyz.srclab.common.cache;

import xyz.srclab.common.cache.local.LocalCache;

public class CacheHelper {

    public static <K, V> Cache<K, V> newLocalCache() {
        return new LocalCache<>();
    }
}
