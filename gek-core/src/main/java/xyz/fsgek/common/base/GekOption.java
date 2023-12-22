package xyz.fsgek.common.base;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This interface represents optional parameter. For example:
 * <pre>
 *     public void doSomething(Object arg, GekOption&lt;?, ?&gt;... options)
 * </pre>
 * The key specifies which option to be set, and the value is actual argument of the option.
 *
 * @param <K> key type
 * @param <V> value type
 */
public interface GekOption<K, V> {

    /**
     * Empty option array.
     */
    GekOption<?, ?>[] EMPTY_OPTIONS = new GekOption<?, ?>[0];

    /**
     * Returns empty option array;
     *
     * @param <K> key type
     * @param <V> value type
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
     * @param <K>   key type
     * @param <V>   value type
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
