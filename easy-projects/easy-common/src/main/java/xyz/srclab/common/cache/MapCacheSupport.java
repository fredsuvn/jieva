package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Cast;

import java.util.Map;
import java.util.function.Supplier;

/**
 * @author sunqian
 */
final class MapCacheSupport {

    static <K, V> Cache<K, V> newMapCache(Map<K, V> map) {
        return new MapCache<>(map);
    }

    static <K, V> Cache<K, V> newMapCache(Supplier<Map<K, V>> mapSupplier) {
        return new MapCache<>(mapSupplier);
    }

    static <K, V> Cache<K, V> newLoadingMapCache(
            Map<K, V> map, CacheLoader<? super K, ? extends V> cacheLoader) {
        return new LoadingMapCache<>(map, cacheLoader);
    }

    static <K, V> Cache<K, V> newLoadingMapCache(
            Supplier<Map<K, V>> mapSupplier, CacheLoader<? super K, ? extends V> cacheLoader) {
        return new LoadingMapCache<>(mapSupplier, cacheLoader);
    }

    private static class MapCache<K, V> implements FixedExpiryCache<K, V> {

        private final Map<K, V> map;

        MapCache(Map<K, V> map) {
            this.map = map;
        }

        MapCache(Supplier<Map<K, V>> mapSupplier) {
            this.map = mapSupplier.get();
        }

        @Override
        public boolean contains(K key) {
            return map.containsKey(key);
        }

        @Override
        public V get(K key) {
            return map.get(key);
        }

        @Override
        public V get(K key, CacheLoader<? super K, ? extends V> loader) {
            try {
                return map.computeIfAbsent(key, new MapCacheLoaderFunction<>(loader));
            } catch (NoResultException e) {
                return null;
            } catch (NotCacheException e) {
                return Cast.asNullable(e.getValue());
            }
        }

        @Override
        public V getOrDefault(K key, @Nullable V defaultValue) {
            return map.getOrDefault(key, defaultValue);
        }

        @Override
        public void put(K key, @Nullable V value) {
            map.put(key, value);
        }

        @Override
        public void invalidate(K key) {
            map.remove(key);
        }

        @Override
        public void invalidateAll() {
            map.clear();
        }
    }

    private static final class LoadingMapCache<K, V> extends MapCache<K, V> {

        private final CacheLoader<? super K, ? extends V> cacheLoader;

        LoadingMapCache(Map<K, V> map, CacheLoader<? super K, ? extends V> cacheLoader) {
            super(map);
            this.cacheLoader = cacheLoader;
        }

        LoadingMapCache(Supplier<Map<K, V>> mapSupplier, CacheLoader<? super K, ? extends V> cacheLoader) {
            super(mapSupplier);
            this.cacheLoader = cacheLoader;
        }

        @Override
        public V get(K key) {
            return super.get(key, cacheLoader);
        }
    }
}
