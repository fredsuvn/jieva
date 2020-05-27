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

    public CacheBuilder<K, V> setMaxSize(long maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    public CacheBuilder<K, V> setConcurrencyLevel(int concurrencyLevel) {
        this.concurrencyLevel = concurrencyLevel;
        return this;
    }

    public CacheBuilder<K, V> setExpiry(CacheExpiry expiry) {
        this.expiry = expiry;
        return this;
    }

    public CacheBuilder<K, V> setLoader(CacheLoader<? super K, @Nullable ? extends V> loader) {
        this.loader = loader;
        return this;
    }

    public CacheBuilder<K, V> setCreateListener(CacheCreateListener<K, V> createListener) {
        this.createListener = createListener;
        return this;
    }

    public CacheBuilder<K, V> setReadListener(CacheReadListener<K, V> readListener) {
        this.readListener = readListener;
        return this;
    }

    public CacheBuilder<K, V> setUpdateListener(CacheUpdateListener<K, V> updateListener) {
        this.updateListener = updateListener;
        return this;
    }

    public CacheBuilder<K, V> setRemoveListener(CacheRemoveListener<K, V> removeListener) {
        this.removeListener = removeListener;
        return this;
    }

    public CacheBuilder<K, V> useGuava() {
        this.useGuava = true;
        return this;
    }

    public CacheBuilder<K, V> useCaffeine() {
        this.useGuava = false;
        return this;
    }

    long getMaxSize() {
        return maxSize;
    }

    int getConcurrencyLevel() {
        return concurrencyLevel;
    }

    @Nullable
    CacheExpiry getExpiry() {
        return expiry;
    }

    @Nullable
    CacheLoader<? super K, @Nullable ? extends V> getLoader() {
        return loader;
    }

    @Nullable
    CacheCreateListener<K, V> getCreateListener() {
        return createListener;
    }

    @Nullable
    CacheReadListener<K, V> getReadListener() {
        return readListener;
    }

    @Nullable
    CacheUpdateListener<K, V> getUpdateListener() {
        return updateListener;
    }

    @Nullable
    CacheRemoveListener<K, V> getRemoveListener() {
        return removeListener;
    }

    boolean isUseGuava() {
        return useGuava;
    }

    public <K1 extends K, V1 extends V> Cache<K1, V1> build() {
        Cache<K, V> cache = useGuava ? new GuavaCache<>(this) : new CaffeineCache<>(this);
        return (Cache<K1, V1>) cache;
    }
}
