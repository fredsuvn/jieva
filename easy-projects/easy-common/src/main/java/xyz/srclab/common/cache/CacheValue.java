package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;

public interface CacheValue<V> {

    @Nullable
    V value();

    @Nullable
    CacheExpiry expiry();
}
