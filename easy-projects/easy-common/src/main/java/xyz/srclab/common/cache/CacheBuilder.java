package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Defaults;
import xyz.srclab.common.cache.listener.CacheCreateListener;
import xyz.srclab.common.cache.listener.CacheReadListener;
import xyz.srclab.common.cache.listener.CacheRemoveListener;
import xyz.srclab.common.cache.listener.CacheUpdateListener;

/**
 * @author sunqian
 */
public final class CacheBuilder<K, V> {

    public static <K, V> CacheBuilder<K, V> newBuilder() {
        return new CacheBuilder<>();
    }

    private long maxSize = Long.MAX_VALUE;
    private int concurrencyLevel = Defaults.CONCURRENCY_LEVEL;

    private @Nullable CacheExpiry expiry;

    private @Nullable CacheLoader<? super K, @Nullable ? extends V> loader;

    private @Nullable CacheCreateListener<K, V> createListener;
    private @Nullable CacheReadListener<K, V> readListener;
    private @Nullable CacheUpdateListener<K, V> updateListener;
    private @Nullable CacheRemoveListener<K, V> removeListener;

    private boolean useGuava = false;

    public CacheBuilder<K, V> maxSize(long maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    public CacheBuilder<K, V> concurrencyLevel(int concurrencyLevel) {
        this.concurrencyLevel = concurrencyLevel;
        return this;
    }

    public CacheBuilder<K, V> expiry(CacheExpiry expiry) {
        this.expiry = expiry;
        return this;
    }

    public CacheBuilder<K, V> loader(CacheLoader<? super K, @Nullable ? extends V> loader) {
        this.loader = loader;
        return this;
    }

    public CacheBuilder<K, V> createListener(CacheCreateListener<K, V> createListener) {
        this.createListener = createListener;
        return this;
    }

    public CacheBuilder<K, V> readListener(CacheReadListener<K, V> readListener) {
        this.readListener = readListener;
        return this;
    }

    public CacheBuilder<K, V> updateListener(CacheUpdateListener<K, V> updateListener) {
        this.updateListener = updateListener;
        return this;
    }

    public CacheBuilder<K, V> removeListener(CacheRemoveListener<K, V> removeListener) {
        this.removeListener = removeListener;
        return this;
    }

    public CacheBuilder<K, V> useGuava(boolean useGuava) {
        this.useGuava = useGuava;
        return this;
    }

    long maxSize() {
        return maxSize;
    }

    int concurrencyLevel() {
        return concurrencyLevel;
    }

    @Nullable
    CacheExpiry expiry() {
        return expiry;
    }

    @Nullable
    CacheLoader<? super K, @Nullable ? extends V> loader() {
        return loader;
    }

    @Nullable
    CacheCreateListener<K, V> createListener() {
        return createListener;
    }

    @Nullable
    CacheReadListener<K, V> readListener() {
        return readListener;
    }

    @Nullable
    CacheUpdateListener<K, V> updateListener() {
        return updateListener;
    }

    @Nullable
    CacheRemoveListener<K, V> removeListener() {
        return removeListener;
    }

    boolean useGuava() {
        return useGuava;
    }

    public <K1 extends K, V1 extends V> Cache<K1, V1> build() {
        Cache<K, V> cache = useGuava ? new GuavaCache<>(this) : new CaffeineCache<>(this);
        return (Cache<K1, V1>) cache;
    }
}
