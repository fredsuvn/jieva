package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;

/**
 * @author sunqian
 */
public interface Cached<V> {

    static <V> Cached<V> of(@Nullable V value) {
        return of(value, null);
    }

    static <V> Cached<V> of(@Nullable V value, @Nullable CacheExpiry expiry) {
        return new Cached<V>() {

            @Override
            public V getValue() {
                return value;
            }

            @Override
            public @Nullable CacheExpiry getExpiry() {
                return expiry;
            }
        };
    }

    @Nullable
    V getValue();

    @Nullable
    CacheExpiry getExpiry();
}
