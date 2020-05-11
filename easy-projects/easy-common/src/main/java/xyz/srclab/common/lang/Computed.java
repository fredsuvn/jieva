package xyz.srclab.common.lang;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Context;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * Caches a computed value, particularly in cases the value is optional and has a heavy process to get.
 *
 * @param <T> computed value
 */
@Immutable
public abstract class Computed<T> implements Supplier<T> {

    static <T> Computed<T> of(Supplier<T> computation) {
        return new SimpleComputed<>(computation);
    }

    static <T> Computed<T> of(long timeoutSeconds, Supplier<T> computation) {
        return new AutoRefreshComputed<>(Duration.ofSeconds(timeoutSeconds), computation);
    }

    static <T> Computed<T> of(Duration timeout, Supplier<T> computation) {
        return new AutoRefreshComputed<>(timeout, computation);
    }

    protected final Supplier<T> computation;

    protected Computed(Supplier<T> computation) {
        this.computation = computation;
    }

    @Override
    public abstract T get();

    public abstract T refreshGet();

    private static final class SimpleComputed<T> extends Computed<T> {

        private @Nullable T cache;

        SimpleComputed(Supplier<T> computation) {
            super(computation);
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
            cache = computation.get();
            return cache;
        }
    }

    private static final class AutoRefreshComputed<T> extends Computed<T> {

        private final Duration timeout;

        private @Nullable T cache;
        private long cacheTimeMillis = 0;

        AutoRefreshComputed(Duration timeout, Supplier<T> computation) {
            super(computation);
            this.timeout = timeout;
        }

        @Override
        public T get() {
            if (this.cache == null) {
                return refreshGet();
            }
            long now = Context.millis();
            return (cacheTimeMillis > 0 && cacheTimeMillis + timeout.toMillis() >= now) ?
                    this.cache : refreshGet();
        }

        @Override
        public T refreshGet() {
            cache = computation.get();
            cacheTimeMillis = Context.millis();
            assert cache != null;
            return cache;
        }
    }
}
