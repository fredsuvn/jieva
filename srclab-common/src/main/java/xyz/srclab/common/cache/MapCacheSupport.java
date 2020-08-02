package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;

import java.util.Map;
import java.util.function.Function;
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

    static <K, V> Cache<K, V> newFunctionMapCache(
            Map<K, V> map, Function<? super K, ? extends V> function) {
        return new FunctionMapCache<>(map, function);
    }

    static <K, V> Cache<K, V> newFunctionMapCache(
            Supplier<Map<K, V>> mapSupplier, Function<? super K, ? extends V> function) {
        return new FunctionMapCache<>(mapSupplier, function);
    }

    static <K, V> Cache<K, V> newLoadingMapCache(
            Map<K, V> map, CacheLoader<? super K, ? extends V> cacheLoader) {
        return new LoadingMapCache<>(map, cacheLoader);
    }

    static <K, V> Cache<K, V> newLoadingMapCache(
            Supplier<Map<K, V>> mapSupplier, CacheLoader<? super K, ? extends V> cacheLoader) {
        return new LoadingMapCache<>(mapSupplier, cacheLoader);
    }

    private static class MapCache<K, V> extends AbstractCache<K, V> implements FixedExpiryCache<K, V> {

        private final Map<K, V> map;

        MapCache(Map<K, V> map) {
            this.map = map;
        }

        MapCache(Supplier<Map<K, V>> mapSupplier) {
            this.map = mapSupplier.get();
        }

        @Override
        public V get(K key) {
            return map.get(key);
        }

        @Override
        public V get(K key, Function<? super K, ? extends V> function) {
            return map.computeIfAbsent(key, function);
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
        public void invalidateALL() {
            map.clear();
        }
    }

    private static final class FunctionMapCache<K, V> extends MapCache<K, V> {

        private final Function<? super K, ? extends V> function;

        FunctionMapCache(Map<K, V> map, Function<? super K, ? extends V> function) {
            super(map);
            this.function = function;
        }

        FunctionMapCache(Supplier<Map<K, V>> mapSupplier, Function<? super K, ? extends V> function) {
            super(mapSupplier);
            this.function = function;
        }

        @Override
        public V get(K key) {
            return get(key, function);
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
            return load(key, cacheLoader);
        }
    }
}
