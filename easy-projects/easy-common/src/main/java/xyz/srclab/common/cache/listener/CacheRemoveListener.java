package xyz.srclab.common.cache.listener;

import xyz.srclab.annotation.Nullable;

/**
 * @author sunqian
 */
public interface CacheRemoveListener<K, V> extends CacheListener<K, V> {

    static <K, V> CacheRemoveListener<K, V> getDefault() {
        return DefaultCacheRemoveListener.INSTANCE;
    }

    void beforeRemove(K key, @Nullable V value);

    void afterRemove(K key, @Nullable V value);

    class DefaultCacheRemoveListener<K, V> implements CacheRemoveListener<K, V> {

        private static final DefaultCacheRemoveListener INSTANCE = new DefaultCacheRemoveListener();

        @Override
        public void beforeRemove(K key, @Nullable V value) {}

        @Override
        public void afterRemove(K key, @Nullable V value) { }
    }
}
