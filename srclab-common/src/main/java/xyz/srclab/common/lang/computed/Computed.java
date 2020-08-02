package xyz.srclab.common.lang.computed;

import xyz.srclab.annotation.Immutable;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * Caches a computed value, particularly in cases the value is optional and has a heavy process to get.
 *
 * @param <T> computed value
 */
@Immutable
public interface Computed<T> {

    static <T> Computed<T> onceOf(Supplier<T> computation) {
        return ComputedSupport.newOnceComputed(computation);
    }

    static <T> Computed<T> multiOf(Supplier<Computed.Result<T>> computation) {
        return ComputedSupport.newMultiComputed(computation);
    }

    static <T> Computed<T> refreshableOf(long timeoutNanos, Supplier<T> computation) {
        return ComputedSupport.newRefreshableComputed(timeoutNanos, computation);
    }

    static <T> Computed<T> refreshableOf(Duration duration, Supplier<T> computation) {
        return ComputedSupport.newRefreshableComputed(duration, computation);
    }

    T get();

    T refreshAndGet();

    interface Result<T> {

        T value();

        Duration expiry();
    }
}
