package xyz.srclab.common.cache.listener;

import xyz.srclab.annotation.Nullable;

/**
 * @author sunqian
 */
public interface CacheReadListener<K, V> extends CacheListener<K, V> {

    static <K, V> CacheReadListener<K, V> getDefault() {
        return DefaultCacheReadListener.INSTANCE;
    }

    void beforeRead(K key);

    void onReadSuccess(K key, @Nullable V value);

    void onReadFailure(K key);

    class DefaultCacheReadListener<K, V> implements CacheReadListener<K, V> {

        private static final DefaultCacheReadListener INSTANCE = new DefaultCacheReadListener();

        @Override
        public void beforeRead(K key) {}

        @Override
        public void onReadSuccess(K key, @Nullable V value) {}

        @Override
        public void onReadFailure(K key) {}
    }
}
