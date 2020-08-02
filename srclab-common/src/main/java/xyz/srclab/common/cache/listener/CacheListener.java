package xyz.srclab.common.cache.listener;

/**
 * @author sunqian
 */
public interface CacheListener<K, V> {

    static <K, V> CacheCreateListener<K, V> emptyCreateListener() {
        return CacheListenerSupport.emptyCreateListener();
    }

    static <K, V> CacheReadListener<K, V> emptyReadListener() {
        return CacheListenerSupport.emptyReadListener();
    }

    static <K, V> CacheUpdateListener<K, V> emptyUpdateListener() {
        return CacheListenerSupport.emptyUpdateListener();
    }

    static <K, V> CacheRemoveListener<K, V> emptyRemoveListener() {
        return CacheListenerSupport.emptyRemoveListener();
    }
}
