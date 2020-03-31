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

abstract class AbstractComputed<T> implements Computed<T> {

    protected final Supplier<T> supplier;
    protected @Nullable T cache;

    protected AbstractComputed(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        if (cache == null) {
            cache = supplier.get();
        }
        return cache;
    }

    @Override
    public void refresh() {
        cache = supplier.get();
    }

    @Override
    public T refreshAndGet() {
        refresh();
        assert cache != null;
        return cache;
    }
}

final class SimpleComputed<T> extends AbstractComputed<T> {

    SimpleComputed(Supplier<T> supplier) {
        super(supplier);
    }
}

final class AutoRefreshComputed<T> extends AbstractComputed<T> {

    private final Duration timeout;
    private long cacheTimeMillis = 0;

    AutoRefreshComputed(Duration timeout, Supplier<T> supplier) {
        super(supplier);
        this.timeout = timeout;
    }

    @Override
    public T get() {
        if (cache == null) {
            refresh();
            return cache;
        }
        long now = TimeHelper.nowMillis();
        return (cacheTimeMillis > 0 && cacheTimeMillis + timeout.toMillis() >= now) ?
                this.cache : refreshAndGet();
    }

    public void refresh() {
        super.refresh();
        cacheTimeMillis = TimeHelper.nowMillis();
    }
}
