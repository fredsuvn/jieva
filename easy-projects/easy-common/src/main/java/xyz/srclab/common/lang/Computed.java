package xyz.srclab.common.lang;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.time.TimeHelper;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;
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

    static Computed<Long> withCounter(long timeoutSeconds) {
        return withCounter(Duration.ofSeconds(timeoutSeconds));
    }

    static Computed<Long> withCounter(Duration timeout) {
        AtomicLong atomicLong = new AtomicLong(0);
        return new AutoRefreshComputed<>(timeout, atomicLong::getAndIncrement);
    }

    @Override
    T get();

    T refreshGet();
}

final class SimpleComputed<T> implements Computed<T> {

    private final Supplier<T> supplier;
    private @Nullable T cache;

    SimpleComputed(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        if (cache == null) {
            return refreshGet();
        }
        return cache;
    }

    @Override
    public T refreshGet() {
        cache = supplier.get();
        return cache;
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
