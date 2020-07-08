package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;

import java.time.Duration;

/**
 * @author sunqian
 */
public interface CacheLoader<K, V> {

    default CacheEntry<K, V> loadEntry(K key) {
        return CacheEntry.newBuilder()
                .key(key)
                .value(loadValue(key))
                .expiryAfterCreate(Duration.ZERO)
                .expiryAfterRead(Duration.ZERO)
                .expiryAfterUpdate(Duration.ZERO)
                .build();
    }

    @Nullable
    V loadValue(K key);
}
