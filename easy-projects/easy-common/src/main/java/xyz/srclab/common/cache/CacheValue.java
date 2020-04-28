package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;

import java.time.Duration;

/**
 * @author sunqian
 */
public interface CacheValue<V> {

    @Nullable
    V getValue();

    Duration getExpiryAfterCreate();

    Duration getExpiryAfterUpdate();

    Duration getExpiryAfterRead();
}
