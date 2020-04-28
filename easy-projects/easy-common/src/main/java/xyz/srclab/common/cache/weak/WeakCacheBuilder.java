package xyz.srclab.common.cache.weak;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.pattern.builder.CachedBuilder;

import java.time.Duration;

/**
 * @author sunqian
 */
public class WeakCacheBuilder<K, V> extends CachedBuilder<WeakCache<K, V>> {

    private @Nullable Duration defaultExpirationPeriod;
    private boolean isSynchronized;

    public WeakCacheBuilder<K, V> setDefaultExpirationPeriod(long defaultExpirationPeriodSeconds) {
        return setDefaultExpirationPeriod(Duration.ofSeconds(defaultExpirationPeriodSeconds));
    }

    public WeakCacheBuilder<K, V> setDefaultExpirationPeriod(Duration defaultExpirationPeriod) {
        this.defaultExpirationPeriod = defaultExpirationPeriod;
        this.updateState();
        return this;
    }

    public WeakCacheBuilder<K, V> setSynchronized(boolean isSynchronized) {
        this.isSynchronized = isSynchronized;
        this.updateState();
        return this;
    }

    @Override
    protected WeakCache<K, V> buildNew() {
        return new WeakCache<>(
                defaultExpirationPeriod == null ? Duration.ZERO : defaultExpirationPeriod,
                isSynchronized
        );
    }


    public <K1 extends K, V1 extends V> WeakCache<K1, V1> build0() {
        return super.build();
    }
}
