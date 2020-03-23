package xyz.srclab.common.lang;

import xyz.srclab.common.time.TimeHelper;

import java.util.function.Supplier;

/**
 * Caches a computed value, particularly in cases the value is optional and has a heavy process to get.
 *
 * @param <T> computed value
 */
public interface Computed<T> extends Supplier<T> {

    static <T> Computed<T> of(Supplier<T> supplier) {
        return new SimpleComputed<>(supplier);
    }

    static <T> Computed<T> of(long timeout, Supplier<T> supplier) {
        return new AutoRefreshComputed<>(timeout, supplier);
    }

    void refresh();

    T refreshAndGet();
}

abstract class AbstractComputed<T> implements Computed<T> {

    protected final Supplier<T> supplier;
    protected T computedCache;

    protected AbstractComputed(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T refreshAndGet() {
        refresh();
        return computedCache;
    }
}

final class SimpleComputed<T> extends AbstractComputed<T> {

    private boolean cached;

    SimpleComputed(Supplier<T> supplier) {
        super(supplier);
    }

    @Override
    public T get() {
        if (!cached) {
            refresh();
        }
        return this.computedCache;
    }

    @Override
    public void refresh() {
        this.computedCache = this.supplier.get();
        cached = true;
    }
}

final class AutoRefreshComputed<T> extends AbstractComputed<T> {

    private final long timeout;

    private long lastComputedTime = 0;

    AutoRefreshComputed(long timeout, Supplier<T> supplier) {
        super(supplier);
        this.timeout = timeout;
    }

    @Override
    public T get() {
        long now = TimeHelper.nowMillis();
        return (lastComputedTime > 0 && lastComputedTime + timeout >= now) ? this.computedCache : refreshAndGet();
    }

    public void refresh() {
        this.computedCache = this.supplier.get();
        lastComputedTime = TimeHelper.nowMillis();
    }
}
