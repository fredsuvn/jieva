package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;

import java.time.Duration;
import java.util.function.Function;

/**
 * @author sunqian
 */
public interface CacheValueFunction<K, V> extends Function<K, @Nullable V> {

    Duration getExpiryAfterCreate();

    Duration getExpiryAfterUpdate();

    Duration getExpiryAfterRead();
}
