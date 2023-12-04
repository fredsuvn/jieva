package xyz.fsgek.common.base;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class represents an option for some service, for example:
 * <pre>
 *     public void doService(Object arg, GekOption&lt;?, ? &gt;... options)
 * </pre>
 * The options should be implemented by service provider.
 *
 * @param <K> type of key
 * @param <V> type of value
 */
public interface GekOption<K, V> {

    /**
     * Returns an option instance of given key and value.
     *
     * @param key   given key
     * @param value given value
     * @param <K>   type of key
     * @param <V>   type of value
     * @return an option instance of given key and value
     */
    static <K, V> GekOption<K, V> of(K key, V value) {
        @Data
        @EqualsAndHashCode
        final class Impl implements GekOption<K, V> {

            @Override
            public K key() {
                return key;
            }

            @Override
            public V value() {
                return value;
            }
        }
        return new Impl();
    }

    /**
     * Returns key.
     *
     * @return key
     */
    K key();

    /**
     * Returns value.
     *
     * @return value
     */
    V value();
}
