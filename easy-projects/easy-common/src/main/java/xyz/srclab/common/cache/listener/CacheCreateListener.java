package xyz.srclab.common.cache.listener;

import xyz.srclab.annotation.Nullable;

/**
 * @author sunqian
 */
public interface CacheCreateListener<K, V> extends CacheListener<K, V> {

    static <K, V> CacheCreateListener<K, V> getDefault() {
        return DefaultCacheCreateListener.INSTANCE;
    }

    void beforeCreate(K key);

    void afterCreate(K key, @Nullable V value);

    class DefaultCacheCreateListener<K, V> implements CacheCreateListener<K, V> {

        private static final DefaultCacheCreateListener INSTANCE = new DefaultCacheCreateListener();

        @Override
        public void beforeCreate(K key) { }

        @Override
        public void afterCreate(K key, @Nullable V value) { }
    }
}
