package xyz.srclab.common.lang.computed;

import xyz.srclab.annotation.Nullable;

import java.util.function.Supplier;

/**
 * @author sunqian
 */
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
