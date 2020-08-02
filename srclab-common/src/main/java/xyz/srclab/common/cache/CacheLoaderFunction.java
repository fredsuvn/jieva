package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;

import java.util.function.Function;

/**
 * @author sunqian
 */
final class CacheLoaderFunction<K, V> implements Function<K, V> {

    private final CacheLoader<K, V> cacheLoader;

    CacheLoaderFunction(CacheLoader<K, V> cacheLoader) {
        this.cacheLoader = cacheLoader;
    }

    @Nullable
    @Override
    public V apply(K k) throws NoResultException, NotCacheException {
        @Nullable CacheLoader.Result<V> result = cacheLoader.load(k);
        if (result == null) {
            throw new NoResultException();
        }
        if (!result.needCache()) {
            throw new NotCacheException(result.value());
        }
        return result.value();
    }
}
