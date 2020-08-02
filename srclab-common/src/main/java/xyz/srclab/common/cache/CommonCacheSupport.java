package xyz.srclab.common.cache;

import com.google.common.collect.MapMaker;
import xyz.srclab.annotation.Nullable;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

/**
 * @author sunqian
 */
final class CommonCacheSupport {

    static <K, V> Cache<K, V> newCommonCache(int concurrentLevel) {
        return new CommonCache<>(concurrentLevel);
    }

    static <K, V> Cache<K, V> newFunctionCache(int concurrentLevel, Function<? super K, ? extends V> function) {
        return new FunctionCommonCache<>(concurrentLevel, function);
    }

    static <K, V> Cache<K, V> newLoadingCache(int concurrentLevel, CacheLoader<? super K, ? extends V> cacheLoader) {
        return new LoadingCommonCache<>(concurrentLevel, cacheLoader);
    }

    private static class CommonCache<K, V> extends AbstractCache<K, V> implements FixedExpiryCache<K, V> {

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
        public V get(K key, Function<? super K, ? extends V> function) {
            return l2.get().computeIfAbsent(key, k -> l1.computeIfAbsent(k, function));
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
        public void invalidateALL() {
            l1.clear();
            l2.get().clear();
        }
    }

    private static final class FunctionCommonCache<K, V> extends CommonCache<K, V> {

        private final Function<? super K, ? extends V> function;

        FunctionCommonCache(int concurrentLevel, Function<? super K, ? extends V> function) {
            super(concurrentLevel);
            this.function = function;
        }

        @Override
        public V get(K key) {
            return get(key, function);
        }
    }

    private static final class LoadingCommonCache<K, V> extends CommonCache<K, V> {

        private final CacheLoader<? super K, ? extends V> cacheLoader;

        LoadingCommonCache(int concurrentLevel, CacheLoader<? super K, ? extends V> cacheLoader) {
            super(concurrentLevel);
            this.cacheLoader = cacheLoader;
        }

        @Override
        public V get(K key) {
            return load(key, cacheLoader);
        }
    }
}
