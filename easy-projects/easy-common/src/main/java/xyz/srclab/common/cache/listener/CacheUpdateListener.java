package xyz.srclab.common.cache.listener;

import xyz.srclab.annotation.Nullable;

/**
 * @author sunqian
 */
public interface CacheUpdateListener<K, V> extends CacheListener<K, V> {

    static <K, V> CacheUpdateListener<K, V> getDefault() {
        return DefaultCacheUpdateListener.INSTANCE;
    }

    void beforeUpdate(K key, @Nullable V oldValue);

    void afterUpdate(K key, @Nullable V oldValue, @Nullable V newValue);

    class DefaultCacheUpdateListener<K, V> implements CacheUpdateListener<K, V> {

        private static final DefaultCacheUpdateListener INSTANCE = new DefaultCacheUpdateListener();

        @Override
        public void beforeUpdate(K key, @Nullable V oldValue) {}

        @Override
        public void afterUpdate(K key, @Nullable V oldValue, @Nullable V newValue) {}
    }
}
