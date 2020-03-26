package xyz.srclab.common.lang;

import xyz.srclab.common.time.TimeHelper;

import java.util.concurrent.TimeUnit;
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
        return new AutoRefreshComputed<>(timeoutSeconds, TimeUnit.SECONDS, supplier);
    }

    static <T> Computed<T> with(long timeout, TimeUnit timeUnit, Supplier<T> supplier) {
        return new AutoRefreshComputed<>(timeout, timeUnit, supplier);
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
    private final TimeUnit timeUnit;

    private long lastComputedTime = 0;

    AutoRefreshComputed(long timeout, TimeUnit timeUnit, Supplier<T> supplier) {
        super(supplier);
        this.timeout = timeout;
        this.timeUnit = timeUnit;
    }

    @Override
    public T get() {
        long now = TimeHelper.nowMillis();
        return (lastComputedTime > 0 && lastComputedTime + timeUnit.toMillis(timeout) >= now) ?
                this.computedCache : refreshAndGet();
    }

    public void refresh() {
        this.computedCache = this.supplier.get();
        lastComputedTime = TimeHelper.nowMillis();
    }
}
