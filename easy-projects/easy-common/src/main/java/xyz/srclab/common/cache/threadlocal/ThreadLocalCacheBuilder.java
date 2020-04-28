package xyz.srclab.common.cache.threadlocal;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.cache.weak.WeakCache;
import xyz.srclab.common.pattern.builder.CachedBuilder;

import java.time.Duration;

/**
 * @author sunqian
 */
public class ThreadLocalCacheBuilder<K, V> extends CachedBuilder<ThreadLocalCache<K, V>> {

    private @Nullable Duration defaultExpirationPeriod;

    public ThreadLocalCacheBuilder<K, V> setDefaultExpirationPeriod(long defaultExpirationPeriodSeconds) {
        return setDefaultExpirationPeriod(Duration.ofSeconds(defaultExpirationPeriodSeconds));
    }

    public ThreadLocalCacheBuilder<K, V> setDefaultExpirationPeriod(Duration defaultExpirationPeriod) {
        this.defaultExpirationPeriod = defaultExpirationPeriod;
        this.updateState();
        return this;
    }

    @Override
    protected ThreadLocalCache<K, V> buildNew() {
        return new WeakCache<>(
                defaultExpirationPeriod == null ? Duration.ZERO : defaultExpirationPeriod,
                isSynchronized
        );
    }
}
