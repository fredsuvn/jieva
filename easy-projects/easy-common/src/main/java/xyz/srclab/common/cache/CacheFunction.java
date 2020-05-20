package xyz.srclab.common.cache;

/**
 * @author sunqian
 */
public interface CacheFunction<K, V> {

    Cached<V> apply(K k);
}
