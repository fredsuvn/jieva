package xyz.srclab.common.cache;

import com.google.common.collect.MapMaker;
import xyz.srclab.annotation.Nullable;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.WeakHashMap;

/**
 * @author sunqian
 */
final class CommonCacheSupport {

    static <K, V> Cache<K, V> newCommonCache(int concurrentLevel) {
        return new CommonCache<>(concurrentLevel);
    }

    static <K, V> Cache<K, V> newLoadingCache(int concurrentLevel, CacheLoader<? super K, ? extends V> cacheLoader) {
        return new LoadingCache<>(concurrentLevel, cacheLoader);
    }

    private static class CommonCache<K, V> implements FixedExpiryCache<K, V> {

        private final Map<K, V> l1;
        private final ThreadLocal<Map<K, V>> l2;

        CommonCache(int concurrentLevel) {
            l1 = new MapMaker()
                    .concurrencyLevel(concurrentLevel)
                    .weakKeys()
                    .makeMap();
            l2 = ThreadLocal.withInitial(WeakHashMap::new);
        }

        @Override
        public boolean contains(K key) {
            return l2.get().containsKey(key) || l1.containsKey(key);
        }

        @Override
        public V get(K key) {
            try {
                return l2.get().computeIfAbsent(key, k -> l1.computeIfAbsent(k, k0 -> {
                    throw new NoResultException();
                }));
            } catch (NoResultException e) {
                return null;
            }
        }

        @Override
        public V get(K key, CacheLoader<? super K, ? extends V> loader) {
            try {
                return l2.get().computeIfAbsent(key, k ->
                        l1.computeIfAbsent(k, new CacheLoaderFunction<>(loader)));
            } catch (NoResultException e) {
                return null;
            } catch (NotCacheException e) {
                return e.getValue();
            }
        }

        @Override
        public V getNonNull(K key, CacheLoader<? super K, ? extends V> loader) throws NoSuchElementException {
            try {
                return l2.get().computeIfAbsent(key, k ->
                        l1.computeIfAbsent(k, new NonNullCacheLoaderFunction<>(loader)));
            } catch (NotCacheException e) {
                return e.getValueNonNull();
            }
        }

        @Override
        public void put(K key, @Nullable V value) {
            l1.put(key, value);
            l2.get().put(key, value);
        }

        @Override
        public void invalidate(K key) {
            l1.remove(key);
            l2.get().remove(key);
        }

        @Override
        public void invalidateAll() {
            l1.clear();
            l2.get().clear();
        }
    }

    private static final class LoadingCache<K, V> extends CommonCache<K, V> {

        private final CacheLoader<? super K, ? extends V> cacheLoader;

        LoadingCache(int concurrentLevel, CacheLoader<? super K, ? extends V> cacheLoader) {
            super(concurrentLevel);
            this.cacheLoader = cacheLoader;
        }

        @Override
        public V get(K key) {
            return super.get(key, cacheLoader);
        }
    }
}
