package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;

import java.util.NoSuchElementException;

/**
 * This class provides a skeletal implementation of the {@link Cache}.
 * It handle these method as {@link SimpleCache}, override them to use advanced feature such as different expiry for
 * each element:
 * {@link #load(Object, CacheLoader)}
 * {@link #loadNonNull(Object, CacheLoader)}
 * {@link #putEntry(CacheEntry)}
 *
 * @author sunqian
 */
public abstract class AbstractCache<K, V> implements Cache<K, V> {

    @Nullable
    @Override
    public V load(K key, CacheLoader<? super K, @Nullable ? extends V> loader) {
        try {
            return get(key, new CacheLoaderFunction<>(loader));
        } catch (NoResultException e) {
            return null;
        } catch (NotCacheException e) {
            return e.getValue();
        }
    }

    @Override
    public V loadNonNull(K key, CacheLoader<? super K, ? extends V> loader) throws NoSuchElementException {
        try {
            return getNonNull(key, new NonNullCacheLoaderFunction<>(loader));
        } catch (NotCacheException e) {
            return e.getValueNonNull();
        }
    }

    @Override
    public void putEntry(CacheEntry<? extends K, ? extends V> entry) {
        put(entry.key(), entry.value());
    }
}
