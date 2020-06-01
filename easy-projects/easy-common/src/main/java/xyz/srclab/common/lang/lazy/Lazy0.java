package xyz.srclab.common.lang.lazy;

import kotlin.LazyKt;

import java.util.function.Supplier;

/**
 * @author sunqian
 */
final class Lazy0 {

    static <T> Lazy<T> newLazy(Supplier<T> supplier) {
        return new LazyImpl<>(supplier);
    }

    private static final class LazyImpl<T> implements Lazy<T> {

        private final kotlin.Lazy<T> kotlinLazy;

        private LazyImpl(Supplier<T> supplier) {
            this.kotlinLazy = LazyKt.lazy(supplier::get);
        }

        @Override
        public T get() {
            return kotlinLazy.getValue();
        }
    }
}
