package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;

/**
 * @author sunqian
 */
final class NotCacheException extends RuntimeException {

    private final @Nullable Object value;

    public NotCacheException(@Nullable Object value) {
        super(null, null, false, false);
        this.value = value;
    }

    @Nullable
    public Object getValue() {
        return value;
    }
}
