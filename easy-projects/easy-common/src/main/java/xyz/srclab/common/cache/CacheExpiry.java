package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.design.builder.ProductCachingBuilder;

import java.time.Duration;
import java.util.Objects;

/**
 * @author sunqian
 */
public interface CacheExpiry {

    static Builder newBuilder() {
        return new Builder();
    }

    @Nullable
    Duration expiryAfterCreate();

    @Nullable
    Duration expiryAfterRead();

    @Nullable
    Duration expiryAfterUpdate();

    final class Builder extends ProductCachingBuilder<CacheExpiry> {

        private @Nullable Duration expiryAfterCreate;
        private @Nullable Duration expiryAfterRead;
        private @Nullable Duration expiryAfterUpdate;

        public Builder expiryAfterCreate(Duration expiryAfterCreate) {
            this.expiryAfterCreate = expiryAfterCreate;
            this.updateState();
            return this;
        }

        public Builder expiryAfterRead(Duration expiryAfterRead) {
            this.expiryAfterRead = expiryAfterRead;
            this.updateState();
            return this;
        }

        public Builder expiryAfterUpdate(Duration expiryAfterUpdate) {
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

            @Nullable
            @Override
            public Duration expiryAfterCreate() {
                return expiryAfterCreate;
            }

            @Nullable
            @Override
            public Duration expiryAfterRead() {
                return expiryAfterRead;
            }

            @Nullable
            @Override
            public Duration expiryAfterUpdate() {
                return expiryAfterUpdate;
            }

            @Override
            public boolean equals(Object object) {
                if (this == object) return true;
                if (object == null || getClass() != object.getClass()) return false;
                CacheExpiryImpl that = (CacheExpiryImpl) object;
                return Objects.equals(expiryAfterCreate, that.expiryAfterCreate) &&
                        Objects.equals(expiryAfterRead, that.expiryAfterRead) &&
                        Objects.equals(expiryAfterUpdate, that.expiryAfterUpdate);
            }

            @Override
            public int hashCode() {
                return Objects.hash(expiryAfterCreate, expiryAfterRead, expiryAfterUpdate);
            }

            @Override
            public String toString() {
                return "(" +
                        "expiryAfterCreate=" + expiryAfterCreate +
                        ", expiryAfterRead=" + expiryAfterRead +
                        ", expiryAfterUpdate=" + expiryAfterUpdate +
                        ')';
            }
        }
    }
}
