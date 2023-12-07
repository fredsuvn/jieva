package xyz.fsgek.common.base;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This interface represents optional parameter. For example:
 * <pre>
 *     public void doSomething(Object arg, GekOption&lt;?, ? &gt;... options)
 * </pre>
 * The key specifies which option to be set, and the value is argument of the option.
 *
 * @param <K> type of key
 * @param <V> type of value
 */
public interface GekOption<K, V> {

    /**
     * Empty option array.
     */
    GekOption<?, ?>[] EMPTY_OPTIONS = new GekOption<?, ?>[0];

    /**
     * Returns empty option array;
     *
     * @param <K> type of key
     * @param <V> type of value
     * @return empty option array
     */
    static <K, V> GekOption<K, V>[] emptyOptions() {
        return Gek.as(EMPTY_OPTIONS);
    }

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
