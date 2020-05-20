package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.pattern.builder.CachedBuilder;

import java.time.Duration;

/**
 * @author sunqian
 */
public interface CacheExpiry {

    Duration getExpiryAfterCreate();

    Duration getExpiryAfterUpdate();

    Duration getExpiryAfterRead();

    final class Builder extends CachedBuilder<CacheExpiry> {

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
            private final Duration expiryAfterCreate;
            private final Duration expiryAfterRead;
            private final Duration expiryAfterUpdate;

            private CacheExpiryImpl(Builder builder) {
                this.expiryAfterCreate = builder.expiryAfterCreate == null ? Duration.ZERO : builder.expiryAfterCreate;
                this.expiryAfterRead = builder.expiryAfterRead == null ? Duration.ZERO : builder.expiryAfterRead;
                this.expiryAfterUpdate = builder.expiryAfterUpdate == null ? Duration.ZERO : builder.expiryAfterUpdate;
            }

            @Override
            public Duration getExpiryAfterCreate() {
                return expiryAfterCreate;
            }

            @Override
            public Duration getExpiryAfterUpdate() {
                return expiryAfterRead;
            }

            @Override
            public Duration getExpiryAfterRead() {
                return expiryAfterUpdate;
            }
        }
    }
}
