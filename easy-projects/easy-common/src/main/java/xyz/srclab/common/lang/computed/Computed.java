package xyz.srclab.common.lang.computed;

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
