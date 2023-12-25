package xyz.fsgek.common.base;

import lombok.Data;
import lombok.EqualsAndHashCode;
import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.common.collect.GekArray;

import java.util.Objects;

/**
 * This interface represents optional parameter. For example:
 * <pre>
 *     public void doSomething(Object arg, GekOption&lt;?, ?&gt;... options)
 * </pre>
 * The key specifies which option to be set, and the value is actual argument of the option.
 *
 * @param <K> type of keys
 * @param <V> type of values
 */
public interface GekOption<K, V> {

    /**
     * Empty option array.
     */
    GekOption<?, ?>[] EMPTY_OPTIONS = new GekOption<?, ?>[0];

    /**
     * Returns empty option array;
     *
     * @param <K> type of keys
     * @param <V> type of values
     * @return empty option array
     */
    static <K, V> GekOption<K, V>[] emptyOptions() {
        return Gek.as(EMPTY_OPTIONS);
    }

    /**
     * Returns option of specified key in given options, or null if not found.
     *
     * @param key     specified key
     * @param options given options
     * @param <K>     type of keys
     * @param <V>     type of values
     * @return option of specified key in given options, or null if not found
     */
    @Nullable
    static <K, V> V get(K key, GekOption<?, ?>... options) {
        if (GekArray.isEmpty(options)) {
            return null;
        }
        for (GekOption<?, ?> option : options) {
            if (option != null && Objects.equals(option.key(), key)) {
                return Gek.as(option.value());
            }
        }
        return null;
    }

    /**
     * Returns an option instance of given key and value.
     *
     * @param key   given key
     * @param value given value
     * @param <K>   type of keys
     * @param <V>   type of values
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
