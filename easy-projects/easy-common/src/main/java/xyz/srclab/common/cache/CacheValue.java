package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Cast;
import xyz.srclab.common.base.Equal;
import xyz.srclab.common.base.Hash;
import xyz.srclab.common.design.builder.BaseProductCachingBuilder;

public interface CacheValue<V> {

    static <V> Builder<V> newBuilder() {
        return new Builder<>();
    }

    static <V> CacheValue<V> of(@Nullable V value) {
        return newBuilder().value(value).build();
    }

    @Nullable
    V value();

    @Nullable
    CacheExpiry expiry();

    class Builder<V> extends BaseProductCachingBuilder<CacheValue<V>> {

        private @Nullable V value;
        private @Nullable CacheExpiry expiry;

        public <V1 extends V> Builder<V1> value(@Nullable V1 value) {
            this.value = value;
            this.updateState();
            return Cast.as(this);
        }

        public <V1 extends V> Builder<V1> expiry(CacheExpiry expiry) {
            this.expiry = expiry;
            this.updateState();
            return Cast.as(this);
        }

        public <V1 extends V> CacheValue<V1> build() {
            return Cast.as(buildCaching());
        }

        @Override
        protected CacheValue<V> buildNew() {
            return new CacheValue<V>() {

                private final @Nullable V value = Builder.this.value;
                private final @Nullable CacheExpiry expiry = Builder.this.expiry;

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
                    return Hash.hash(value);
                }

                @Override
                public boolean equals(@Nullable Object obj) {
                    if (obj == null || !getClass().equals(obj.getClass())) {
                        return false;
                    }
                    CacheValue<?> that = (CacheValue<?>) obj;
                    return Equal.equals(value, that.value())
                            && Equal.equals(expiry, that.expiry());
                }

                @Override
                public String toString() {
                    return value
                            + "(" +
                            "expiry: " + expiry
                            + ")";
                }
            };
        }
    }
}
