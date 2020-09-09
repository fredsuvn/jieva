package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.As;
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
        return As.nullable(value);
    }

    public <V> V getValueNonNull() {
        @Nullable V v = getValue();
        Check.checkNull(v != null);
        return v;
    }
}
