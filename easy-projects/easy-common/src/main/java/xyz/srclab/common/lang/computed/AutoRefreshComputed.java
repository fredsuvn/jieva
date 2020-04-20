package xyz.srclab.common.lang.computed;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.time.TimeHelper;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * @author sunqian
 */
final class AutoRefreshComputed<T> implements Computed<T> {

    private final Supplier<T> supplier;
    private final Duration timeout;

    private @Nullable T cache;
    private long cacheTimeMillis = 0;

    AutoRefreshComputed(Duration timeout, Supplier<T> supplier) {
        this.supplier = supplier;
        this.timeout = timeout;
    }

    @Override
    public T get() {
        if (this.cache == null) {
            return refreshGet();
        }
        long now = TimeHelper.nowMillis();
        return (cacheTimeMillis > 0 && cacheTimeMillis + timeout.toMillis() >= now) ?
                this.cache : refreshGet();
    }

    @Override
    public T refreshGet() {
        cache = supplier.get();
        cacheTimeMillis = TimeHelper.nowMillis();
        assert cache != null;
        return cache;
    }
}
