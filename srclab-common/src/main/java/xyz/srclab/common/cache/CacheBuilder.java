package xyz.srclab.common.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Cast;
import xyz.srclab.common.base.Defaults;
import xyz.srclab.common.cache.listener.CacheCreateListener;
import xyz.srclab.common.cache.listener.CacheReadListener;
import xyz.srclab.common.cache.listener.CacheRemoveListener;
import xyz.srclab.common.cache.listener.CacheUpdateListener;
import xyz.srclab.common.design.builder.BaseProductCachingBuilder;

/**
 * @author sunqian
 */
public final class CacheBuilder<K, V> extends BaseProductCachingBuilder<Cache<K, V>> {

    public static <K, V> CacheBuilder<K, V> newBuilder() {
        return new CacheBuilder<>();
    }

    private long maxSize = Long.MAX_VALUE;
    private int concurrencyLevel = Defaults.CONCURRENCY_LEVEL;

    private @Nullable CacheExpiry expiry;

    private @Nullable CacheLoader<? super K, @Nullable ? extends V> loader;

    private @Nullable CacheCreateListener<? super K, ? super V> createListener;
    private @Nullable CacheReadListener<? super K, ? super V> readListener;
    private @Nullable CacheUpdateListener<? super K, ? super V> updateListener;
    private @Nullable CacheRemoveListener<? super K, ? super V> removeListener;

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

    public <K1 extends K, V1 extends V> CacheBuilder<K1, V1> loader(
            CacheLoader<? super K1, @Nullable ? extends V1> loader) {
        this.loader = Cast.as(loader);
        return Cast.as(this);
    }

    public <K1 extends K, V1 extends V> CacheBuilder<K1, V1> createListener(
            CacheCreateListener<? super K1, ? super V1> createListener) {
        this.createListener = Cast.as(createListener);
        return Cast.as(this);
    }

    public <K1 extends K, V1 extends V> CacheBuilder<K1, V1> readListener(
            CacheReadListener<? super K1, ? super V1> readListener) {
        this.readListener = Cast.as(readListener);
        return Cast.as(this);
    }

    public <K1 extends K, V1 extends V> CacheBuilder<K1, V1> updateListener(
            CacheUpdateListener<? super K1, ? super V1> updateListener) {
        this.updateListener = Cast.as(updateListener);
        return Cast.as(this);
    }

    public <K1 extends K, V1 extends V> CacheBuilder<K1, V1> removeListener(
            CacheRemoveListener<? super K1, ? super V1> removeListener) {
        this.removeListener = Cast.as(removeListener);
        return Cast.as(this);
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
    CacheCreateListener<? super K, ? super V> createListener() {
        return createListener;
    }

    @Nullable
    CacheReadListener<? super K, ? super V> readListener() {
        return readListener;
    }

    @Nullable
    CacheUpdateListener<? super K, ? super V> updateListener() {
        return updateListener;
    }

    @Nullable
    CacheRemoveListener<? super K, ? super V> removeListener() {
        return removeListener;
    }

    boolean useGuava() {
        return useGuava;
    }

    @Override
    protected Cache<K, V> buildNew() {
        if (useGuava) {
            com.google.common.cache.CacheBuilder<K, Object> guavaBuilder = GuavaCacheSupport.toGuavaCacheBuilder(this);
            if (loader == null) {
                com.google.common.cache.Cache<K, Object> guavaCache = guavaBuilder.build();
                return Cache.guavaCache(guavaCache);
            } else {
                com.google.common.cache.LoadingCache<K, Object> loadingGuavaCache =
                        guavaBuilder.build(GuavaCacheSupport.toGuavaCacheLoader(loader));
                return Cache.loadingGuavaCache(loadingGuavaCache);
            }
        } else {
            Caffeine<K, Object> caffeine = CaffeineCacheSupport.toCaffeineCacheBuilder(this);
            if (loader == null) {
                com.github.benmanes.caffeine.cache.Cache<K, Object> caffeineCache = caffeine.build();
                return Cache.caffeineCache(caffeineCache);
            } else {
                com.github.benmanes.caffeine.cache.LoadingCache<K, Object> loadingCaffeineCache =
                        caffeine.build(CaffeineCacheSupport.toCaffeineCacheLoader(loader));
                return Cache.loadingCaffeineCache(loadingCaffeineCache);
            }
        }
    }

    public <K1 extends K, V1 extends V> Cache<K1, V1> build() {
        return Cast.as(buildCaching());
    }
}
