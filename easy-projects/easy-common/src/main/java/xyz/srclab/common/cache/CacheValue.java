package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Cast;
import xyz.srclab.common.base.Equal;
import xyz.srclab.common.base.Hash;
import xyz.srclab.common.base.ToString;
import xyz.srclab.common.design.builder.CachedBuilder;

import java.time.Duration;

/**
 * @author sunqian
 */
public interface CacheValue<V> {

    static <V> Builder<V> newBuilder() {
        return new Builder<>();
    }

    static <V> CacheValue<V> of(@Nullable V value) {
        return newBuilder()
                .value(value)
                .build();
    }

    @Nullable
    V value();

    /**
     * 0 for default.
     *
     * @return
     */
    Duration expiryAfterCreate();

    /**
     * 0 for default.
     *
     * @return
     */
    Duration expiryAfterRead();

    /**
     * 0 for default.
     *
     * @return
     */
    Duration expiryAfterUpdate();

    class Builder<V> extends CachedBuilder<CacheValue<V>> {

        private @Nullable V value;
        private Duration expiryAfterCreate = Duration.ZERO;
        private Duration expiryAfterRead = Duration.ZERO;
        private Duration expiryAfterUpdate = Duration.ZERO;

        public <V1 extends V> Builder<V1> value(@Nullable V1 value) {
            this.value = value;
            return Cast.as(this);
        }

        public <V1 extends V> Builder<V1> expiryAfterCreate(Duration expiryAfterCreate) {
            this.expiryAfterCreate = expiryAfterCreate;
            return Cast.as(this);
        }

        public <V1 extends V> Builder<V1> expiryAfterRead(Duration expiryAfterRead) {
            this.expiryAfterRead = expiryAfterRead;
            return Cast.as(this);
        }

        public <V1 extends V> Builder<V1> expiryAfterUpdate(Duration expiryAfterUpdate) {
            this.expiryAfterUpdate = expiryAfterUpdate;
            return Cast.as(this);
        }

        public <V1 extends V> CacheValue<V1> build() {
            return Cast.as(buildCached());
        }

        @Override
        protected CacheValue<V> buildNew() {
            return new CacheValue<V>() {

                private final @Nullable V value = Builder.this.value;
                private final Duration expiryAfterCreate = Builder.this.expiryAfterCreate;
                private final Duration expiryAfterRead = Builder.this.expiryAfterRead;
                private final Duration expiryAfterUpdate = Builder.this.expiryAfterUpdate;

                @Nullable
                @Override
                public V value() {
                    return value;
                }

                @Override
                public Duration expiryAfterCreate() {
                    return expiryAfterCreate;
                }

                @Override
                public Duration expiryAfterRead() {
                    return expiryAfterRead;
                }

                @Override
                public Duration expiryAfterUpdate() {
                    return expiryAfterUpdate;
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
                            && Equal.equals(expiryAfterCreate, that.expiryAfterCreate())
                            && Equal.equals(expiryAfterRead, that.expiryAfterRead())
                            && Equal.equals(expiryAfterUpdate, that.expiryAfterUpdate());
                }

                @Override
                public String toString() {
                    return ToString.toString(value)
                            + "(expiryAfterCreate: " + expiryAfterCreate
                            + ", expiryAfterRead: " + expiryAfterRead
                            + ", expiryAfterUpdate: " + expiryAfterUpdate
                            + ")";
                }
            };
        }
    }
}
