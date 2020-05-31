package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;

public interface CacheEntry<K, V> {

    K key();

    @Nullable
    V value();

    @Nullable
    CacheExpiry expiry();
}
