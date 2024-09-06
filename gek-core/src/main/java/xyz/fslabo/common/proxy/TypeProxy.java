package xyz.fslabo.common.proxy;

import xyz.fslabo.annotations.ThreadSafe;

/**
 * Type proxy, to create proxy instance of proxied type.
 *
 * @param <T> proxied type
 * @author fredsuvn
 */
@ThreadSafe
public interface TypeProxy<T> {

    /**
     * Returns a new builder of {@link TypeProxy}.
     *
     * @param <T> proxied type
     * @return new builder of {@link TypeProxy}.
     * @see TypeProxyBuilder
     */
    static <T> TypeProxyBuilder<T> newBuilder() {
        return new TypeProxyBuilder<>();
    }

    /**
     * Constructs and returns new instance of this proxy class.
     *
     * @return new instance of this proxy class
     */
    T newInstance();
}
