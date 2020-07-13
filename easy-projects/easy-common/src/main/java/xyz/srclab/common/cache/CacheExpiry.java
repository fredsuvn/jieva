package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.design.builder.BaseProductCachingBuilder;

import java.time.Duration;

/**
 * @author sunqian
 */
public interface CacheExpiry {

    static Builder newBuilder() {
        return new Builder();
    }

    @Nullable
    Duration getExpiryAfterCreate();

    @Nullable
    Duration getExpiryAfterUpdate();

    @Nullable
    Duration getExpiryAfterRead();

    final class Builder extends BaseProductCachingBuilder<CacheExpiry> {

        private @Nullable Duration expiryAfterCreate;
        private @Nullable Duration expiryAfterRead;
        private @Nullable Duration expiryAfterUpdate;

        public Builder setExpiryAfterCreate(Duration expiryAfterCreate) {
            this.expiryAfterCreate = expiryAfterCreate;
            this.updateState();
            return this;
        }

        public Builder setExpiryAfterRead(Duration expiryAfterRead) {
            this.expiryAfterRead = expiryAfterRead;
            this.updateState();
            return this;
        }

        public Builder setExpiryAfterUpdate(Duration expiryAfterUpdate) {
            this.expiryAfterUpdate = expiryAfterUpdate;
            this.updateState();
            return this;
        }

        @Override
        protected CacheExpiry buildNew() {
            return new CacheExpiryImpl(this);
        }

        private static final class CacheExpiryImpl implements CacheExpiry {

            private final @Nullable Duration expiryAfterCreate;
            private final @Nullable Duration expiryAfterRead;
            private final @Nullable Duration expiryAfterUpdate;

            private CacheExpiryImpl(Builder builder) {
                this.expiryAfterCreate = builder.expiryAfterCreate;
                this.expiryAfterRead = builder.expiryAfterRead;
                this.expiryAfterUpdate = builder.expiryAfterUpdate;
            }

            @Override
            @Nullable
            public Duration getExpiryAfterCreate() {
                return expiryAfterCreate;
            }

            @Override
            @Nullable
            public Duration getExpiryAfterUpdate() {
                return expiryAfterRead;
            }

            @Override
            @Nullable
            public Duration getExpiryAfterRead() {
                return expiryAfterUpdate;
            }
        }
    }
}
