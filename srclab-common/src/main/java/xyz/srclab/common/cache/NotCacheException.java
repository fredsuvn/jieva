package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Cast;
import xyz.srclab.common.base.Check;

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
    public <V> V getValue() {
        return Cast.asNullable(value);
    }

    public <V> V getValueNonNull() {
        @Nullable V v = getValue();
        Check.checkNull(v != null);
        return v;
    }
}
