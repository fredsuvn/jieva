package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Cast;
import xyz.srclab.common.base.Check;
import xyz.srclab.common.base.Equal;
import xyz.srclab.common.base.Hash;
import xyz.srclab.common.design.builder.BaseProductCachingBuilder;

public interface CacheEntry<K, V> {

    static <K, V> Builder<K, V> newBuilder() {
        return new Builder<>();
    }

    static <K, V> Builder<K, V> newBuilder(K key) {
        return new Builder<>(key);
    }

    static <K, V> CacheEntry<K, V> of(K key, @Nullable V value) {
        return newBuilder(key).value(value).build();
    }

    K key();

    @Nullable
    V value();

    @Nullable
    CacheExpiry expiry();

    class Builder<K, V> extends BaseProductCachingBuilder<CacheEntry<K, V>> {

        private @Nullable K key;
        private @Nullable V value;
        private @Nullable CacheExpiry expiry;

        public Builder() {
        }

        public Builder(K key) {
            this.key = key;
        }

        public <K1 extends K, V1 extends V> Builder<K1, V1> key(K1 key) {
            this.key = key;
            this.updateState();
            return Cast.as(this);
        }

        public <K1 extends K, V1 extends V> Builder<K1, V1> value(@Nullable V1 value) {
            this.value = value;
            this.updateState();
            return Cast.as(this);
        }

        public <K1 extends K, V1 extends V> Builder<K1, V1> expiry(@Nullable CacheExpiry expiry) {
            this.expiry = expiry;
            this.updateState();
            return Cast.as(this);
        }

        public <K1 extends K, V1 extends V> CacheEntry<K1, V1> build() {
            return Cast.as(buildCaching());
        }

        @Override
        protected CacheEntry<K, V> buildNew() {
            Check.checkArguments(Builder.this.key != null);
            return new CacheEntry<K, V>() {

                private final K key = Builder.this.key;
                private final @Nullable V value = Builder.this.value;
                private final @Nullable CacheExpiry expiry = Builder.this.expiry;

                @Override
                public K key() {
                    return key;
                }

                @Nullable
                @Override
                public V value() {
                    return value;
                }

                @Nullable
                @Override
                public CacheExpiry expiry() {
                    return expiry;
                }

                @Override
                public int hashCode() {
                    return Hash.hash(key, value, expiry);
                }

                @Override
                public boolean equals(@Nullable Object obj) {
                    if (obj == null || !getClass().equals(obj.getClass())) {
                        return false;
                    }
                    CacheEntry<?, ?> that = (CacheEntry<?, ?>) obj;
                    return Equal.equals(key, that.key())
                            && Equal.equals(value, that.value())
                            && Equal.equals(expiry, that.expiry());
                }

                @Override
                public String toString() {
                    return "(" +
                            key + ", " +
                            value + ", " +
                            expiry +
                            ")";
                }
            };
        }
    }
}
