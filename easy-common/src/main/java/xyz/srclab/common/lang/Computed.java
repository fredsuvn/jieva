package xyz.srclab.common.lang;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.time.TimeHelper;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * Caches a computed value, particularly in cases the value is optional and has a heavy process to get.
 *
 * @param <T> computed value
 */
public interface Computed<T> extends Supplier<T> {

    static <T> Computed<T> with(Supplier<T> supplier) {
        return new SimpleComputed<>(supplier);
    }

    static <T> Computed<T> with(long timeoutSeconds, Supplier<T> supplier) {
        return new AutoRefreshComputed<>(Duration.ofSeconds(timeoutSeconds), supplier);
    }

    static <T> Computed<T> with(Duration timeout, Supplier<T> supplier) {
        return new AutoRefreshComputed<>(timeout, supplier);
    }

    void refresh();

    T refreshAndGet();
}

final class SimpleComputed<T> extends CachedNonNull<T> implements Computed<T> {

    private final Supplier<T> supplier;

    SimpleComputed(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        return getNonNull();
    }

    @Override
    public void refresh() {
        this.refreshNonNull();
    }

    @Override
    public T refreshAndGet() {
        refresh();
        assert cache != null;
        return cache;
    }

    @Override
    protected T newNonNull() {
        return supplier.get();
    }
}

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
            refresh();
            return cache;
        }
        long now = TimeHelper.nowMillis();
        return (cacheTimeMillis > 0 && cacheTimeMillis + timeout.toMillis() >= now) ?
                this.cache : refreshAndGet();
    }

    public void refresh() {
        cache = supplier.get();
        cacheTimeMillis = TimeHelper.nowMillis();
    }

    @Override
    public T refreshAndGet() {
        refresh();
        assert cache != null;
        return cache;
    }
}
