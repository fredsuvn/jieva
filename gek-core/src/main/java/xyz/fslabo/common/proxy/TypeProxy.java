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
     * @see ProxyBuilder
     */
    static <T> ProxyBuilder<T> newBuilder() {
        return new ProxyBuilder<>();
    }

    /**
     * Constructs and returns a new proxy instance.
     *
     * @return a new proxy instance
     */
    T newInstance();
}
