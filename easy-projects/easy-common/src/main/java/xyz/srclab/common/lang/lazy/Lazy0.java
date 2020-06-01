package xyz.srclab.common.lang.lazy;

import xyz.srclab.annotation.Nullable;

import java.util.function.Supplier;

/**
 * @author sunqian
 */
final class Lazy0 {

    static <T> Lazy<T> newLazy(Supplier<T> supplier) {
        return new LazyImpl<>(supplier);
    }

    private static final class LazyImpl<T> implements Lazy<T> {

        private final Supplier<T> supplier;

        private volatile @Nullable T value;

        private LazyImpl(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        @Override
        public T get() {
            if (value == null) {
                synchronized (this) {
                    if (value == null) {
                        value = supplier.get();
                    }
                }
            }
            return value;
        }
    }
}
