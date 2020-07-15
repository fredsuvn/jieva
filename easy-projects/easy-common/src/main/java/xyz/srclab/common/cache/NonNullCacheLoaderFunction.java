package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Check;

import java.util.NoSuchElementException;
import java.util.function.Function;

/**
 * @author sunqian
 */
final class NonNullCacheLoaderFunction<K, V> implements Function<K, V> {

    private final CacheLoader<K, V> cacheLoader;

    NonNullCacheLoaderFunction(CacheLoader<K, V> cacheLoader) {
        this.cacheLoader = cacheLoader;
    }

    @Override
    public V apply(K k) throws NoSuchElementException, NotCacheException {
        @Nullable CacheLoader.Result<V> result = cacheLoader.load(k);
        Check.checkElement(result != null, k);
        @Nullable V v = result.value();
        Check.checkElement(v != null, k);
        if (!result.needCache()) {
            throw new NotCacheException(v);
        }
        return v;
    }
}
