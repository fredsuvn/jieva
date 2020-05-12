package xyz.srclab.common.lang;

import xyz.srclab.annotation.Nullable;

/**
 * @author sunqian
 */
public interface Entry<K, V> {

    static <K, V> Entry<K, V> of(K key, @Nullable V value) {
        return new Entry<K, V>() {
            @Override
            public K getKey() {
                return key;
            }

            @Override
            public V getValue() {
                return value;
            }
        };
    }

    K getKey();

    @Nullable
    V getValue();
}
