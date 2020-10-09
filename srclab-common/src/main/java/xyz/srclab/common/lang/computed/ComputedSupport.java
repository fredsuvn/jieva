package xyz.srclab.common.lang.computed;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Computed;
import xyz.srclab.common.base.Current;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * @author sunqian
 */
final class ComputedSupport {

    static <T> Computed<T> newOnceComputed(Supplier<T> computation) {
        return new OnceComputed<>(computation);
    }

    static <T> Computed<T> newMultiComputed(Supplier<Computed.Result<T>> computation) {
        return new MultiComputed<>(computation);
    }

    static <T> Computed<T> newRefreshableComputed(long timeoutNanos, Supplier<T> computation) {
        return new RefreshableComputed<>(timeoutNanos, computation);
    }

    static <T> Computed<T> newRefreshableComputed(Duration duration, Supplier<T> computation) {
        return new RefreshableComputed<>(duration.toNanos(), computation);
    }

    private static final class OnceComputed<T> implements Computed<T> {

        private final Supplier<T> computation;
        private @Nullable T value;

        private OnceComputed(Supplier<T> computation) {
            this.computation = computation;
        }

        @Override
        public T get() {
            if (value == null) {
                return refreshAndGet();
            }
            return value;
        }

        @Override
        public T refreshAndGet() {
            value = computation.get();
            return value;
        }
    }

    private static final class MultiComputed<T> implements Computed<T> {

        private final Supplier<Result<T>> computation;

        private @Nullable T value;
        private long lastMillis = 0;
        private long timeoutNanos = 0;

        private MultiComputed(Supplier<Result<T>> computation) {
            this.computation = computation;
        }

        @Override
        public T get() {
            if (this.value == null || lastMillis == 0 || timeoutNanos == 0) {
                return refreshAndGet();
            }
            long now = Current.milliseconds();
            long diff = now - lastMillis;
            return diff * 1000000 <= timeoutNanos ? value : refreshAndGet();
        }

        @Override
        public T refreshAndGet() {
            Result<T> result = computation.get();
            lastMillis = Current.milliseconds();
            value = result.value();
            timeoutNanos = result.expiry().toNanos();
            return value;
        }
    }

    private static final class RefreshableComputed<T> implements Computed<T> {

        private final Supplier<T> computation;
        private final long timeoutNanos;

        private @Nullable T value;
        private long lastMillis = 0;

        private RefreshableComputed(long timeoutNanos, Supplier<T> computation) {
            this.computation = computation;
            this.timeoutNanos = timeoutNanos;
        }

        @Override
        public T get() {
            if (this.value == null || lastMillis == 0) {
                return refreshAndGet();
            }
            long now = Current.milliseconds();
            long diff = now - lastMillis;
            return diff * 1000000 <= timeoutNanos ? value : refreshAndGet();
        }

        @Override
        public T refreshAndGet() {
            value = computation.get();
            lastMillis = Current.milliseconds();
            assert value != null;
            return value;
        }
    }
}
