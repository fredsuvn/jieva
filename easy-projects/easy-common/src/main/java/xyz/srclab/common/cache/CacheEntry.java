package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Cast;
import xyz.srclab.common.base.Check;
import xyz.srclab.common.design.builder.CachedBuilder;

import java.time.Duration;

/**
 * @author sunqian
 */
public interface CacheEntry<K, V> {

    static <K, V> Builder<K, V> newBuilder() {
        return new Builder<>();
    }

    K key();

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

    class Builder<K, V> extends CachedBuilder<CacheEntry<K, V>> {

        private @Nullable K key;
        private @Nullable V value;
        private @Nullable Duration expiryAfterCreate;
        private @Nullable Duration expiryAfterRead;
        private @Nullable Duration expiryAfterUpdate;

        public <K1 extends K, V1 extends V> Builder<K1, V1> key(K1 key) {
            this.key = key;
            return Cast.as(this);
        }

        public <K1 extends K, V1 extends V> Builder<K1, V1> value(@Nullable V1 value) {
            this.value = value;
            return Cast.as(this);
        }

        public <K1 extends K, V1 extends V> Builder<K1, V1> expiryAfterCreate(Duration expiryAfterCreate) {
            this.expiryAfterCreate = expiryAfterCreate;
            return Cast.as(this);
        }

        public <K1 extends K, V1 extends V> Builder<K1, V1> expiryAfterRead(Duration expiryAfterRead) {
            this.expiryAfterRead = expiryAfterRead;
            return Cast.as(this);
        }

        public <K1 extends K, V1 extends V> Builder<K1, V1> expiryAfterUpdate(Duration expiryAfterUpdate) {
            this.expiryAfterUpdate = expiryAfterUpdate;
            return Cast.as(this);
        }

        public <K1 extends K, V1 extends V> CacheEntry<K1, V1> build() {
            return Cast.as(buildCached());
        }

        @Override
        protected CacheEntry<K, V> buildNew() {
            Check.checkArguments(key != null, "Cache key must not be null");
            Check.checkArguments(expiryAfterCreate != null,
                    "Cache expiryAfterCreate must not be null");
            Check.checkArguments(expiryAfterRead != null,
                    "Cache expiryAfterRead must not be null");
            Check.checkArguments(expiryAfterUpdate != null,
                    "Cache expiryAfterUpdate must not be null");

            return new CacheEntry<K, V>() {

                private final K key = Builder.this.key;
                private final @Nullable V value = Builder.this.value;
                private final Duration expiryAfterCreate = Builder.this.expiryAfterCreate;
                private final Duration expiryAfterRead = Builder.this.expiryAfterRead;
                private final Duration expiryAfterUpdate = Builder.this.expiryAfterUpdate;

                @Override
                public K key() {
                    return key;
                }

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
            };
        }
    }
}
